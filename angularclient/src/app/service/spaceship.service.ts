import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Spaceship } from '../model/spaceship';

@Injectable({
  providedIn: 'root'
})
export class SpaceshipService {
  private apiUrl = 'http://localhost:8080/api/spaceships';

  constructor(private http: HttpClient) { }

  getAllSpaceships(page: number, size: number, name?: string, sort: string = 'id', direction: string = 'asc'): Observable<{ content: Spaceship[], totalPages: number, currentPage: number, totalItems: number, size: number }> {
    const adjustedPage = Math.max(0, page); // Ensure page is never negative
    let params = new HttpParams()
      .set('page', adjustedPage.toString())
      .set('size', size.toString())
      .set('sort', sort)
      .set('direction', direction);
    if (name) {
      params = params.set('name', name);
    }
    console.log(`Sending request to ${this.apiUrl} with params:`, params.toString());
    return this.http.get<any>(this.apiUrl, { params }).pipe(
      map((data: { content: any[], totalPages: number, currentPage: number, totalItems: number, size: number, number: number }) => {
        //console.log('Raw API Response:', JSON.stringify(data, null, 2));
        if (data && Array.isArray(data.content)) {
          const result = {
            content: data.content.map((item: any) => ({
              id: item.id,
              name: item.name,
              model: item.model
            })),
            totalPages: data.totalPages || 1,
            currentPage: data.currentPage !== undefined ? data.currentPage : data.number,
            totalItems: data.totalItems,
            size: data.size
          };
          //console.log('Processed API Response:', JSON.stringify(result, null, 2));
          console.log(`Retrieved page ${result.currentPage+1} of ${result.totalPages}, total items: ${result.totalItems}, page size: ${result.size}`);
          return result;
        } else {
          console.error('Unexpected response format:', data);
          throw new Error('Unexpected response format');
        }
      }),
      catchError(error => {
        console.error('Error fetching spaceships:', error);
        return throwError(() => new Error('Failed to fetch spaceships'));
      })
    );
  }

  getAllSpaceshipsWithoutPagination(): Observable<Spaceship[]> {
    return this.http.get<Spaceship[]>(`${this.apiUrl}/all`);
  }

  getSpaceshipById(id: number): Observable<Spaceship> {
    return this.http.get<Spaceship>(`${this.apiUrl}/${id}`);
  }

  createSpaceship(spaceship: Spaceship): Observable<Spaceship> {
    return this.http.post<Spaceship>(`${this.apiUrl}/create`, spaceship);
  }

  updateSpaceship(id: number, spaceship: Spaceship): Observable<Spaceship> {
    return this.http.put<Spaceship>(`${this.apiUrl}/${id}`, spaceship);
  }

  deleteSpaceship(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
