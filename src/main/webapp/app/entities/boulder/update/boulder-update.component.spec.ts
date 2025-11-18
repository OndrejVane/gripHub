import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IWall } from 'app/entities/wall/wall.model';
import { WallService } from 'app/entities/wall/service/wall.service';
import { BoulderService } from '../service/boulder.service';
import { IBoulder } from '../boulder.model';
import { BoulderFormService } from './boulder-form.service';

import { BoulderUpdateComponent } from './boulder-update.component';

describe('Boulder Management Update Component', () => {
  let comp: BoulderUpdateComponent;
  let fixture: ComponentFixture<BoulderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let boulderFormService: BoulderFormService;
  let boulderService: BoulderService;
  let wallService: WallService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [BoulderUpdateComponent],
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
      .overrideTemplate(BoulderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BoulderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    boulderFormService = TestBed.inject(BoulderFormService);
    boulderService = TestBed.inject(BoulderService);
    wallService = TestBed.inject(WallService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Wall query and add missing value', () => {
      const boulder: IBoulder = { id: 3829 };
      const wall: IWall = { id: 23247 };
      boulder.wall = wall;

      const wallCollection: IWall[] = [{ id: 23247 }];
      jest.spyOn(wallService, 'query').mockReturnValue(of(new HttpResponse({ body: wallCollection })));
      const additionalWalls = [wall];
      const expectedCollection: IWall[] = [...additionalWalls, ...wallCollection];
      jest.spyOn(wallService, 'addWallToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ boulder });
      comp.ngOnInit();

      expect(wallService.query).toHaveBeenCalled();
      expect(wallService.addWallToCollectionIfMissing).toHaveBeenCalledWith(
        wallCollection,
        ...additionalWalls.map(expect.objectContaining),
      );
      expect(comp.wallsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const boulder: IBoulder = { id: 3829 };
      const wall: IWall = { id: 23247 };
      boulder.wall = wall;

      activatedRoute.data = of({ boulder });
      comp.ngOnInit();

      expect(comp.wallsSharedCollection).toContainEqual(wall);
      expect(comp.boulder).toEqual(boulder);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBoulder>>();
      const boulder = { id: 24244 };
      jest.spyOn(boulderFormService, 'getBoulder').mockReturnValue(boulder);
      jest.spyOn(boulderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ boulder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: boulder }));
      saveSubject.complete();

      // THEN
      expect(boulderFormService.getBoulder).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(boulderService.update).toHaveBeenCalledWith(expect.objectContaining(boulder));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBoulder>>();
      const boulder = { id: 24244 };
      jest.spyOn(boulderFormService, 'getBoulder').mockReturnValue({ id: null });
      jest.spyOn(boulderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ boulder: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: boulder }));
      saveSubject.complete();

      // THEN
      expect(boulderFormService.getBoulder).toHaveBeenCalled();
      expect(boulderService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBoulder>>();
      const boulder = { id: 24244 };
      jest.spyOn(boulderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ boulder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(boulderService.update).toHaveBeenCalled();
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
