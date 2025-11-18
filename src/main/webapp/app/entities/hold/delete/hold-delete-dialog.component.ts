import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IHold } from '../hold.model';
import { HoldService } from '../service/hold.service';

@Component({
  templateUrl: './hold-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class HoldDeleteDialogComponent {
  hold?: IHold;

  protected holdService = inject(HoldService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.holdService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
