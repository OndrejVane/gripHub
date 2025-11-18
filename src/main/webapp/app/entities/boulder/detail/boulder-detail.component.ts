import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IBoulder } from '../boulder.model';

@Component({
  selector: 'jhi-boulder-detail',
  templateUrl: './boulder-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class BoulderDetailComponent {
  boulder = input<IBoulder | null>(null);

  previousState(): void {
    window.history.back();
  }
}
