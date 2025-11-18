import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IWall } from '../wall.model';
import { WallService } from '../service/wall.service';

@Component({
  templateUrl: './wall-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class WallDeleteDialogComponent {
  wall?: IWall;

  protected wallService = inject(WallService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.wallService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
