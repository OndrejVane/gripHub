import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IBoulder } from 'app/entities/boulder/boulder.model';
import { BoulderService } from 'app/entities/boulder/service/boulder.service';
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
  let boulderService: BoulderService;

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
    boulderService = TestBed.inject(BoulderService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boulder query and add missing value', () => {
      const wall: IWall = { id: 5682 };
      const boulders: IBoulder[] = [{ id: 24244 }];
      wall.boulders = boulders;

      const boulderCollection: IBoulder[] = [{ id: 24244 }];
      jest.spyOn(boulderService, 'query').mockReturnValue(of(new HttpResponse({ body: boulderCollection })));
      const additionalBoulders = [...boulders];
      const expectedCollection: IBoulder[] = [...additionalBoulders, ...boulderCollection];
      jest.spyOn(boulderService, 'addBoulderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ wall });
      comp.ngOnInit();

      expect(boulderService.query).toHaveBeenCalled();
      expect(boulderService.addBoulderToCollectionIfMissing).toHaveBeenCalledWith(
        boulderCollection,
        ...additionalBoulders.map(expect.objectContaining),
      );
      expect(comp.bouldersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const wall: IWall = { id: 5682 };
      const boulder: IBoulder = { id: 24244 };
      wall.boulders = [boulder];

      activatedRoute.data = of({ wall });
      comp.ngOnInit();

      expect(comp.bouldersSharedCollection).toContainEqual(boulder);
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

  describe('Compare relationships', () => {
    describe('compareBoulder', () => {
      it('should forward to boulderService', () => {
        const entity = { id: 24244 };
        const entity2 = { id: 3829 };
        jest.spyOn(boulderService, 'compareBoulder');
        comp.compareBoulder(entity, entity2);
        expect(boulderService.compareBoulder).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
