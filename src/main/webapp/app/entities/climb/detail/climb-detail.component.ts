import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IClimb } from '../climb.model';

@Component({
  selector: 'jhi-climb-detail',
  templateUrl: './climb-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ClimbDetailComponent {
  climb = input<IClimb | null>(null);

  previousState(): void {
    window.history.back();
  }
}
