import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ClimbDetailComponent } from './climb-detail.component';

describe('Climb Management Detail Component', () => {
  let comp: ClimbDetailComponent;
  let fixture: ComponentFixture<ClimbDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClimbDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./climb-detail.component').then(m => m.ClimbDetailComponent),
              resolve: { climb: () => of({ id: 8939 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ClimbDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClimbDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load climb on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ClimbDetailComponent);

      // THEN
      expect(instance.climb()).toEqual(expect.objectContaining({ id: 8939 }));
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
