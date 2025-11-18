import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IHold } from '../hold.model';

@Component({
  selector: 'jhi-hold-detail',
  templateUrl: './hold-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class HoldDetailComponent {
  hold = input<IHold | null>(null);

  previousState(): void {
    window.history.back();
  }
}
