import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IWall } from 'app/entities/wall/wall.model';
import { WallService } from 'app/entities/wall/service/wall.service';
import { HoldService } from '../service/hold.service';
import { IHold } from '../hold.model';
import { HoldFormService } from './hold-form.service';

import { HoldUpdateComponent } from './hold-update.component';

describe('Hold Management Update Component', () => {
  let comp: HoldUpdateComponent;
  let fixture: ComponentFixture<HoldUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let holdFormService: HoldFormService;
  let holdService: HoldService;
  let wallService: WallService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HoldUpdateComponent],
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
      .overrideTemplate(HoldUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HoldUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    holdFormService = TestBed.inject(HoldFormService);
    holdService = TestBed.inject(HoldService);
    wallService = TestBed.inject(WallService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Wall query and add missing value', () => {
      const hold: IHold = { id: 23041 };
      const wall: IWall = { id: 23247 };
      hold.wall = wall;

      const wallCollection: IWall[] = [{ id: 23247 }];
      jest.spyOn(wallService, 'query').mockReturnValue(of(new HttpResponse({ body: wallCollection })));
      const additionalWalls = [wall];
      const expectedCollection: IWall[] = [...additionalWalls, ...wallCollection];
      jest.spyOn(wallService, 'addWallToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ hold });
      comp.ngOnInit();

      expect(wallService.query).toHaveBeenCalled();
      expect(wallService.addWallToCollectionIfMissing).toHaveBeenCalledWith(
        wallCollection,
        ...additionalWalls.map(expect.objectContaining),
      );
      expect(comp.wallsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const hold: IHold = { id: 23041 };
      const wall: IWall = { id: 23247 };
      hold.wall = wall;

      activatedRoute.data = of({ hold });
      comp.ngOnInit();

      expect(comp.wallsSharedCollection).toContainEqual(wall);
      expect(comp.hold).toEqual(hold);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHold>>();
      const hold = { id: 20041 };
      jest.spyOn(holdFormService, 'getHold').mockReturnValue(hold);
      jest.spyOn(holdService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hold });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: hold }));
      saveSubject.complete();

      // THEN
      expect(holdFormService.getHold).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(holdService.update).toHaveBeenCalledWith(expect.objectContaining(hold));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHold>>();
      const hold = { id: 20041 };
      jest.spyOn(holdFormService, 'getHold').mockReturnValue({ id: null });
      jest.spyOn(holdService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hold: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: hold }));
      saveSubject.complete();

      // THEN
      expect(holdFormService.getHold).toHaveBeenCalled();
      expect(holdService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHold>>();
      const hold = { id: 20041 };
      jest.spyOn(holdService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hold });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(holdService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareWall', () => {
      it('should forward to wallService', () => {
        const entity = { id: 23247 };
        const entity2 = { id: 5682 };
        jest.spyOn(wallService, 'compareWall');
        comp.compareWall(entity, entity2);
        expect(wallService.compareWall).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
