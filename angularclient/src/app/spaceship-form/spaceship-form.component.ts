import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Spaceship } from '../model/spaceship';
import { SpaceshipService } from '../service/spaceship.service';
import { CommonModule } from '@angular/common';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-spaceship-form',
  templateUrl: './spaceship-form.component.html',
  styleUrls: ['./spaceship-form.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule]
})
export class SpaceshipFormComponent implements OnInit {
  spaceshipForm: FormGroup;
  @Input() isEditMode = false;
  @Input() spaceshipId: number | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private spaceshipService: SpaceshipService,
    public activeModal: NgbActiveModal
  ) {
    this.spaceshipForm = this.formBuilder.group({
      name: ['', Validators.required],
      model: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.isEditMode && this.spaceshipId) {
      this.loadSpaceship(this.spaceshipId);
    }
  }

  loadSpaceship(id: number): void {
    this.spaceshipService.getSpaceshipById(id).subscribe(
      (spaceship: Spaceship) => {
        this.spaceshipForm.patchValue(spaceship);
      },
      error => {
        console.error('Error loading spaceship', error);
      }
    );
  }

  onSubmit(): void {
    if (this.spaceshipForm.valid) {
      const spaceship: Spaceship = this.spaceshipForm.value;
      if (this.isEditMode && this.spaceshipId) {
        this.spaceshipService.updateSpaceship(this.spaceshipId, spaceship).subscribe(
          () => {
            this.activeModal.close('Submit click');
          },
          error => {
            console.error('Error updating spaceship', error);
          }
        );
      } else {
        this.spaceshipService.createSpaceship(spaceship).subscribe(
          () => {
            this.activeModal.close('Submit click');
          },
          error => {
            console.error('Error creating spaceship', error);
          }
        );
      }
    }
  }

  closeModal() {
    this.activeModal.close('Close click');
  }
}
