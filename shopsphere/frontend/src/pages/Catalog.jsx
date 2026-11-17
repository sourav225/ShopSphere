import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import {
  Alert, Button, Card, CardActions, CardContent, CardMedia, Chip, Grid, MenuItem, Pagination,
  Snackbar, Stack, TextField, Typography,
} from '@mui/material';
import { errMsg, useAddToCartMutation, useGetProductsQuery } from '../api';

const CATEGORIES = ['Electronics', 'Fashion', 'Home'];

export default function Catalog() {
  const [q, setQ] = useState('');
  const [category, setCategory] = useState('');
  const [page, setPage] = useState(0);
  const [toast, setToast] = useState(null);
  const user = useSelector((s) => s.auth.user);
  const navigate = useNavigate();
  const { data, isLoading, error } = useGetProductsQuery({ q, category, page, size: 8 });
  const [addToCart] = useAddToCartMutation();

  const add = async (id) => {
    if (!user) return navigate('/login');
    try {
      await addToCart({ productId: id, quantity: 1 }).unwrap();
      setToast({ severity: 'success', msg: 'Added to cart' });
    } catch (e) {
      setToast({ severity: 'error', msg: errMsg(e) });
    }
  };

  return (
    <>
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 3 }}>
        <TextField fullWidth label="Search products" value={q} onChange={(e) => { setQ(e.target.value); setPage(0); }} />
        <TextField select sx={{ minWidth: 180 }} label="Category" value={category} onChange={(e) => { setCategory(e.target.value); setPage(0); }}>
          <MenuItem value="">All</MenuItem>
          {CATEGORIES.map((c) => <MenuItem key={c} value={c}>{c}</MenuItem>)}
        </TextField>
      </Stack>

      {error && <Alert severity="error">{errMsg(error)}</Alert>}
      {isLoading && <Typography>Loading…</Typography>}

      <Grid container spacing={3}>
        {data?.content.map((p) => (
          <Grid item xs={12} sm={6} md={3} key={p.id}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardMedia sx={{ height: 140, bgcolor: 'grey.200' }} image={p.imageUrl || undefined} title={p.name} />
              <CardContent sx={{ flexGrow: 1 }}>
                <Chip size="small" label={p.category || 'General'} sx={{ mb: 1 }} />
                <Typography variant="h6">{p.name}</Typography>
                <Typography variant="body2" color="text.secondary">{p.description}</Typography>
                <Typography variant="h6" color="primary" sx={{ mt: 1 }}>${Number(p.price).toFixed(2)}</Typography>
                <Typography variant="caption" color={p.stock > 0 ? 'text.secondary' : 'error'}>
                  {p.stock > 0 ? `${p.stock} in stock` : 'Out of stock'}
                </Typography>
              </CardContent>
              <CardActions>
                <Button fullWidth variant="contained" disabled={p.stock === 0} onClick={() => add(p.id)}>Add to cart</Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      {data && data.totalPages > 1 && (
        <Stack alignItems="center" sx={{ mt: 4 }}>
          <Pagination count={data.totalPages} page={page + 1} onChange={(_, v) => setPage(v - 1)} color="primary" />
        </Stack>
      )}
      <Snackbar open={!!toast} autoHideDuration={2500} onClose={() => setToast(null)}>
        {toast ? <Alert severity={toast.severity} variant="filled">{toast.msg}</Alert> : undefined}
      </Snackbar>
    </>
  );
}
