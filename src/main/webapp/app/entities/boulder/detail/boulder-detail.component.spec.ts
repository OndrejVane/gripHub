import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { BoulderDetailComponent } from './boulder-detail.component';

describe('Boulder Management Detail Component', () => {
  let comp: BoulderDetailComponent;
  let fixture: ComponentFixture<BoulderDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BoulderDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./boulder-detail.component').then(m => m.BoulderDetailComponent),
              resolve: { boulder: () => of({ id: 24244 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(BoulderDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BoulderDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load boulder on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', BoulderDetailComponent);

      // THEN
      expect(instance.boulder()).toEqual(expect.objectContaining({ id: 24244 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
