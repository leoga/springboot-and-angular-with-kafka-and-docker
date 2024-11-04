import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { SpaceshipService } from '../service/spaceship.service';
import { Spaceship } from '../model/spaceship';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';

import { SpaceshipFormComponent } from '../spaceship-form/spaceship-form.component';

@Component({
  selector: 'app-spaceship-list',
  templateUrl: './spaceship-list.component.html',
  styleUrls: ['./spaceship-list.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, SpaceshipFormComponent, FormsModule],
})
export class SpaceshipListComponent implements OnInit {
  spaceships: Spaceship[] = [];
  loading = true;
  error: string | null = null;
  currentPage = 0;
  totalPages = 0;
  pageSize = 10; // Adjust this value as needed
  searchId: number | null = null;
  searchName: string = '';
  sortColumn: string = 'id';
  sortDirection: 'asc' | 'desc' = 'asc';

  constructor(
    private spaceshipService: SpaceshipService,
    private changeDetectorRef: ChangeDetectorRef,
    private modalService: NgbModal
  ) {}

  ngOnInit() {
    this.loadSpaceships();
  }

  sort(column: string) {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.loadSpaceships();
  }

  getSortIcon(column: string): string {
    if (this.sortColumn !== column) {
      return 'bi-sort-alpha-down';
    }
    return this.sortDirection === 'asc' ? 'bi-sort-down' : 'bi-sort-up';
  }

  searchSpaceship() {
    this.loading = true;
    this.error = null;
    if (this.searchId) {
      this.spaceshipService.getSpaceshipById(this.searchId).subscribe({
        next: (spaceship) => {
          this.spaceships = [spaceship];
          this.loading = false;
          this.totalPages = 1;
          this.currentPage = 0;
        },
        error: (err) => {
          this.error = 'Spaceship not found';
          this.loading = false;
          this.spaceships = [];
          this.totalPages = 1;
          this.currentPage = 0;
        }
      });
    } else if (this.searchName) {
      this.loadSpaceships();
    } else {
      this.loadSpaceships();
    }
  }

  clearSearch() {
    this.searchId = null;
    this.searchName = '';
    this.error = null;
    this.loadSpaceships();
  }

  loadSpaceships() {
    this.loading = true;
    this.error = null;
    console.log(`Loading spaceships for page ${this.currentPage}, size ${this.pageSize}, name: ${this.searchName}, sort: ${this.sortColumn} ${this.sortDirection}`);
    this.spaceshipService.getAllSpaceships(this.currentPage, this.pageSize, this.searchName, this.sortColumn, this.sortDirection).subscribe({
      next: (response) => {
        if (response && Array.isArray(response.content)) {
          this.spaceships = response.content;
          this.totalPages = response.totalPages;

          // Adjust current page if it's out of bounds
          if (this.currentPage >= this.totalPages && this.totalPages > 0) {
            this.currentPage = this.totalPages - 1;
            // Reload with the adjusted page
            this.loadSpaceships();
            return;
          }

          // If current page is empty and there are other pages, move to the last non-empty page
          if (this.spaceships.length === 0 && this.totalPages > 1) {
            this.currentPage = this.totalPages - 1;
            this.loadSpaceships();
            return;
          }

          this.currentPage = response.currentPage;
          this.loading = false;
        } else {
          console.error('Invalid response format:', response);
          this.error = 'Invalid response from server. Please try again later.';
          this.spaceships = [];
          this.totalPages = 0;
          this.currentPage = 0;
        }

        // Force change detection
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        console.error('Error fetching spaceships:', error);
        this.error = 'Failed to load spaceships. Please try again later.';
        this.loading = false;
        this.spaceships = [];
        this.totalPages = 0;
        this.currentPage = 0;
        this.changeDetectorRef.detectChanges();
      },
      complete: () => {
        this.loading = false;
        this.changeDetectorRef.detectChanges();
      }
    });
  }

  onPageChange(page: number) {
    console.log(`Page changed to ${page}`);
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadSpaceships();
    } else {
      console.warn(`Invalid page number: ${page}. Current page: ${this.currentPage}, Total pages: ${this.totalPages}`);
    }
  }

  get currentPageDisplay(): number {
    return this.currentPage + 1;
  }

  deleteSpaceship(spaceship: Spaceship) {
    if (spaceship && spaceship.id !== undefined) {

      this.spaceshipService.deleteSpaceship(spaceship.id).subscribe({
        next: () => {
          const isLastItemOnPage = this.spaceships.length === 1;

          if (isLastItemOnPage && this.currentPage > 0) {
            console.log('Last item on page deleted, moving to previous page');
            this.currentPage--;
          } else {
            console.log('Item deleted, staying on current page');
          }

          console.log(`Current page after deletion logic: ${this.currentPage}`);
          this.loadSpaceships();
        },
        error: (error) => {
          console.error('Error deleting spaceship:', error);
        }
      });
    } else {
      console.error('Cannot delete spaceship with undefined id');
    }
  }

  editSpaceship(spaceship: Spaceship) {
    const modalRef = this.modalService.open(SpaceshipFormComponent);
    modalRef.componentInstance.isEditMode = true;
    modalRef.componentInstance.spaceshipId = spaceship.id;
    modalRef.result.then(
      () => {
        console.log('Modal closed');
      },
      (reason) => {
        console.log('Modal dismissed with reason:', reason);
      }
    ).finally(() => {
      this.loadSpaceships(); // Refresh the list in all cases
    });
  }

  createSpaceship() {
    const modalRef = this.modalService.open(SpaceshipFormComponent);
    modalRef.componentInstance.isEditMode = false;
    modalRef.result.then(
      () => {
        console.log('Modal closed');
      },
      (reason) => {
        console.log('Modal dismissed with reason:', reason);
      }
    ).finally(() => {
      this.loadSpaceships(); // Refresh the list in all cases
    });
  }
}
