import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBoulder } from '../boulder.model';
import { BoulderService } from '../service/boulder.service';

@Component({
  templateUrl: './boulder-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BoulderDeleteDialogComponent {
  boulder?: IBoulder;

  protected boulderService = inject(BoulderService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.boulderService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
