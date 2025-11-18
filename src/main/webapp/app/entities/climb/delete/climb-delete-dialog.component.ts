import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IClimb } from '../climb.model';
import { ClimbService } from '../service/climb.service';

@Component({
  templateUrl: './climb-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ClimbDeleteDialogComponent {
  climb?: IClimb;

  protected climbService = inject(ClimbService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.climbService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
