import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Alert, Box, Button, Paper, Stack, TextField, Typography } from '@mui/material';
import { errMsg, useLoginMutation, useRegisterMutation } from '../api';
import { setCredentials } from '../authSlice';

export default function Login() {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [login, { isLoading: l, error: le }] = useLoginMutation();
  const [register, { isLoading: r, error: re }] = useRegisterMutation();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const error = mode === 'login' ? le : re;

  const submit = async (e) => {
    e.preventDefault();
    try {
      const res = await (mode === 'login' ? login(form) : register(form)).unwrap();
      dispatch(setCredentials(res));
      navigate(res.role === 'ADMIN' ? '/admin' : '/');
    } catch { /* shown via error state */ }
  };

  return (
    <Box sx={{ maxWidth: 420, mx: 'auto' }}>
      <Paper component="form" onSubmit={submit} sx={{ p: 4 }}>
        <Stack spacing={2}>
          <Typography variant="h5">{mode === 'login' ? 'Welcome back' : 'Create your account'}</Typography>
          {error && <Alert severity="error">{errMsg(error)}</Alert>}
          {mode === 'register' && (
            <TextField label="Name" required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          )}
          <TextField label="Email" type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
          <TextField label="Password" type="password" required inputProps={{ minLength: 6 }} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
          <Button type="submit" variant="contained" size="large" disabled={l || r}>
            {mode === 'login' ? 'Login' : 'Register'}
          </Button>
          <Button onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
            {mode === 'login' ? "New here? Create an account" : 'Have an account? Login'}
          </Button>
        </Stack>
      </Paper>
    </Box>
  );
}
