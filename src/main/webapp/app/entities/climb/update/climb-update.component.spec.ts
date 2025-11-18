import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IBoulder } from 'app/entities/boulder/boulder.model';
import { BoulderService } from 'app/entities/boulder/service/boulder.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IClimb } from '../climb.model';
import { ClimbService } from '../service/climb.service';
import { ClimbFormService } from './climb-form.service';

import { ClimbUpdateComponent } from './climb-update.component';

describe('Climb Management Update Component', () => {
  let comp: ClimbUpdateComponent;
  let fixture: ComponentFixture<ClimbUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let climbFormService: ClimbFormService;
  let climbService: ClimbService;
  let boulderService: BoulderService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ClimbUpdateComponent],
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
      .overrideTemplate(ClimbUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClimbUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    climbFormService = TestBed.inject(ClimbFormService);
    climbService = TestBed.inject(ClimbService);
    boulderService = TestBed.inject(BoulderService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Boulder query and add missing value', () => {
      const climb: IClimb = { id: 16534 };
      const boulder: IBoulder = { id: 24244 };
      climb.boulder = boulder;

      const boulderCollection: IBoulder[] = [{ id: 24244 }];
      jest.spyOn(boulderService, 'query').mockReturnValue(of(new HttpResponse({ body: boulderCollection })));
      const additionalBoulders = [boulder];
      const expectedCollection: IBoulder[] = [...additionalBoulders, ...boulderCollection];
      jest.spyOn(boulderService, 'addBoulderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ climb });
      comp.ngOnInit();

      expect(boulderService.query).toHaveBeenCalled();
      expect(boulderService.addBoulderToCollectionIfMissing).toHaveBeenCalledWith(
        boulderCollection,
        ...additionalBoulders.map(expect.objectContaining),
      );
      expect(comp.bouldersSharedCollection).toEqual(expectedCollection);
    });

    it('should call User query and add missing value', () => {
      const climb: IClimb = { id: 16534 };
      const climbedBy: IUser = { id: '1344246c-16a7-46d1-bb61-2043f965c8d5' };
      climb.climbedBy = climbedBy;

      const userCollection: IUser[] = [{ id: '1344246c-16a7-46d1-bb61-2043f965c8d5' }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [climbedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ climb });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const climb: IClimb = { id: 16534 };
      const boulder: IBoulder = { id: 24244 };
      climb.boulder = boulder;
      const climbedBy: IUser = { id: '1344246c-16a7-46d1-bb61-2043f965c8d5' };
      climb.climbedBy = climbedBy;

      activatedRoute.data = of({ climb });
      comp.ngOnInit();

      expect(comp.bouldersSharedCollection).toContainEqual(boulder);
      expect(comp.usersSharedCollection).toContainEqual(climbedBy);
      expect(comp.climb).toEqual(climb);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClimb>>();
      const climb = { id: 8939 };
      jest.spyOn(climbFormService, 'getClimb').mockReturnValue(climb);
      jest.spyOn(climbService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ climb });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: climb }));
      saveSubject.complete();

      // THEN
      expect(climbFormService.getClimb).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(climbService.update).toHaveBeenCalledWith(expect.objectContaining(climb));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClimb>>();
      const climb = { id: 8939 };
      jest.spyOn(climbFormService, 'getClimb').mockReturnValue({ id: null });
      jest.spyOn(climbService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ climb: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: climb }));
      saveSubject.complete();

      // THEN
      expect(climbFormService.getClimb).toHaveBeenCalled();
      expect(climbService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClimb>>();
      const climb = { id: 8939 };
      jest.spyOn(climbService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ climb });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(climbService.update).toHaveBeenCalled();
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

    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: '1344246c-16a7-46d1-bb61-2043f965c8d5' };
        const entity2 = { id: '1e61df13-b2d3-459d-875e-5607a4ccdbdb' };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
