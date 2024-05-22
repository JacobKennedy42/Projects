import { Action, createFeatureSelector, createReducer, createSelector, on } from '@ngrx/store';
import { loginFailure, loginSuccess, logout, setCompany } from './auth.actions';
import Company from '../models/Company';

export interface AuthState {
  loggedIn: boolean;
  user: any;
  error: any;
  company: Company | null;
}

export const initialState: AuthState = {
  loggedIn: false,
  user: null,
  error: null,
  company: null
};

const _authReducer = createReducer(
  initialState,
  on(loginSuccess, (state, { loginSuccessResp }) => {
    return {
      ...state,
      loggedIn: true,
      user: loginSuccessResp,
      error: null,
      company: loginSuccessResp.companies[0]
    };
  }), 
  on(loginFailure, (state, { loginFailureResp }) => {
    return {
      ...state,
      loggedIn: false,
      user: null,
      error: loginFailureResp,
    };
  }), 
  on(logout, state => {
    return {
      ...state,
      loggedIn: false,
      user: null,
      error: null
    };
  }),
  on(setCompany, (state, { company }) => {
    return {
      ...state,
      company
    };
  }), 
)

export function authReducer(state: AuthState | undefined, action: Action) {
  return _authReducer(state, action);
}

export const selectAuthState = createFeatureSelector<AuthState>('auth');

export const selectLoggedIn = createSelector(selectAuthState, s => s.loggedIn);

export const selectUser = createSelector( selectAuthState, s => s.user);

export const selectIsAdmin = createSelector( selectAuthState, s => s.user && s.user.admin);

export const selectTeams = createSelector( selectAuthState, s => s.user.teams);

export const selectFirstLast = createSelector( selectAuthState, s => s.user.profile.firstName + ' ' + s.user.profile.lastName.charAt(0) + '.');

export const selectCompany = createSelector( selectAuthState, s => s.company)

export const selectCompanyId = createSelector( selectAuthState, s => s.company?.id ?? 0)

export const selectUserId = createSelector( selectAuthState, s => s.user.id)

export const selectAdminTeamId = createSelector( selectAuthState, s => s.user.teams[0].id)
