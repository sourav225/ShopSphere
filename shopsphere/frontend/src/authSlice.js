import { createSlice } from '@reduxjs/toolkit';

const saved = (() => { try { return JSON.parse(localStorage.getItem('auth')); } catch { return null; } })();

const authSlice = createSlice({
  name: 'auth',
  initialState: { token: saved?.token || null, user: saved?.user || null },
  reducers: {
    setCredentials(state, { payload }) {
      state.token = payload.token;
      state.user = { email: payload.email, name: payload.name, role: payload.role };
      localStorage.setItem('auth', JSON.stringify(state));
    },
    logout(state) {
      state.token = null; state.user = null;
      localStorage.removeItem('auth');
    },
  },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;
