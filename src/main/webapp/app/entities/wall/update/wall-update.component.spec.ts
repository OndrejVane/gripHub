import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { WallService } from '../service/wall.service';
import { IWall } from '../wall.model';
import { WallFormService } from './wall-form.service';

import { WallUpdateComponent } from './wall-update.component';

describe('Wall Management Update Component', () => {
  let comp: WallUpdateComponent;
  let fixture: ComponentFixture<WallUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let wallFormService: WallFormService;
  let wallService: WallService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [WallUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(WallUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WallUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    wallFormService = TestBed.inject(WallFormService);
    wallService = TestBed.inject(WallService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const wall: IWall = { id: 5682 };

      activatedRoute.data = of({ wall });
      comp.ngOnInit();

      expect(comp.wall).toEqual(wall);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWall>>();
      const wall = { id: 23247 };
      jest.spyOn(wallFormService, 'getWall').mockReturnValue(wall);
      jest.spyOn(wallService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ wall });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: wall }));
      saveSubject.complete();

      // THEN
      expect(wallFormService.getWall).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(wallService.update).toHaveBeenCalledWith(expect.objectContaining(wall));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWall>>();
      const wall = { id: 23247 };
      jest.spyOn(wallFormService, 'getWall').mockReturnValue({ id: null });
      jest.spyOn(wallService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ wall: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: wall }));
      saveSubject.complete();

      // THEN
      expect(wallFormService.getWall).toHaveBeenCalled();
      expect(wallService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWall>>();
      const wall = { id: 23247 };
      jest.spyOn(wallService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ wall });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(wallService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
