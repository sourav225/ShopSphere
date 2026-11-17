import { useState } from 'react';
import {
  Alert, Button, Card, CardContent, Dialog, DialogActions, DialogContent, DialogTitle, Grid, IconButton,
  MenuItem, Paper, Select, Stack, Tab, Table, TableBody, TableCell, TableHead, TableRow, Tabs, TextField, Typography,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import {
  errMsg, useCreateProductMutation, useDeleteProductMutation, useGetAdminOrdersQuery, useGetProductsQuery,
  useGetStatsQuery, useUpdateOrderStatusMutation, useUpdateProductMutation,
} from '../api';

const STATUSES = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];
const empty = { name: '', description: '', price: '', stock: 0, category: '', imageUrl: '' };

function Dashboard() {
  const { data } = useGetStatsQuery();
  if (!data) return null;
  const cards = [
    ['Revenue (paid)', `$${Number(data.revenue).toFixed(2)}`], ['Orders', data.orders],
    ['Products', data.products], ['Users', data.users],
  ];
  return (
    <>
      <Grid container spacing={2}>
        {cards.map(([label, value]) => (
          <Grid item xs={6} md={3} key={label}>
            <Card><CardContent><Typography color="text.secondary">{label}</Typography><Typography variant="h4">{value}</Typography></CardContent></Card>
          </Grid>
        ))}
      </Grid>
      <Paper sx={{ p: 2, mt: 3 }}>
        <Typography variant="h6" sx={{ mb: 1 }}>Orders by status</Typography>
        <Stack direction="row" spacing={4} flexWrap="wrap">
          {Object.entries(data.ordersByStatus).map(([k, v]) => (
            <div key={k}><Typography variant="h5">{v}</Typography><Typography variant="caption">{k}</Typography></div>
          ))}
        </Stack>
      </Paper>
    </>
  );
}

function Products() {
  const { data } = useGetProductsQuery({ q: '', category: '', page: 0, size: 50 });
  const [create] = useCreateProductMutation();
  const [update] = useUpdateProductMutation();
  const [del] = useDeleteProductMutation();
  const [form, setForm] = useState(null);
  const [error, setError] = useState(null);

  const save = async () => {
    const body = { ...form, price: Number(form.price), stock: Number(form.stock) };
    try {
      await (form.id ? update(body) : create(body)).unwrap();
      setForm(null); setError(null);
    } catch (e) { setError(errMsg(e)); }
  };
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  return (
    <>
      <Button variant="contained" sx={{ mb: 2 }} onClick={() => setForm(empty)}>Add product</Button>
      <Paper>
        <Table size="small">
          <TableHead><TableRow><TableCell>Name</TableCell><TableCell>Category</TableCell><TableCell align="right">Price</TableCell><TableCell align="right">Stock</TableCell><TableCell /></TableRow></TableHead>
          <TableBody>
            {data?.content.map((p) => (
              <TableRow key={p.id}>
                <TableCell>{p.name}</TableCell><TableCell>{p.category}</TableCell>
                <TableCell align="right">${Number(p.price).toFixed(2)}</TableCell><TableCell align="right">{p.stock}</TableCell>
                <TableCell align="right">
                  <IconButton onClick={() => setForm(p)}><EditIcon /></IconButton>
                  <IconButton onClick={() => del(p.id)}><DeleteIcon /></IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
      <Dialog open={!!form} onClose={() => setForm(null)} fullWidth maxWidth="sm">
        <DialogTitle>{form?.id ? 'Edit product' : 'New product'}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1 }}>
            {error && <Alert severity="error">{error}</Alert>}
            <TextField label="Name" value={form?.name || ''} onChange={set('name')} />
            <TextField label="Description" multiline minRows={2} value={form?.description || ''} onChange={set('description')} />
            <TextField label="Price" type="number" value={form?.price ?? ''} onChange={set('price')} />
            <TextField label="Stock" type="number" value={form?.stock ?? 0} onChange={set('stock')} />
            <TextField label="Category" value={form?.category || ''} onChange={set('category')} />
            <TextField label="Image URL" value={form?.imageUrl || ''} onChange={set('imageUrl')} />
          </Stack>
        </DialogContent>
        <DialogActions><Button onClick={() => setForm(null)}>Cancel</Button><Button variant="contained" onClick={save}>Save</Button></DialogActions>
      </Dialog>
    </>
  );
}

function AdminOrders() {
  const { data } = useGetAdminOrdersQuery();
  const [setStatus] = useUpdateOrderStatusMutation();
  return (
    <Paper>
      <Table size="small">
        <TableHead><TableRow><TableCell>#</TableCell><TableCell>Customer</TableCell><TableCell align="right">Total</TableCell><TableCell>Payment</TableCell><TableCell>Status</TableCell></TableRow></TableHead>
        <TableBody>
          {data?.map((o) => (
            <TableRow key={o.id}>
              <TableCell>{o.id}</TableCell><TableCell>{o.userEmail}</TableCell>
              <TableCell align="right">${Number(o.total).toFixed(2)}</TableCell><TableCell>{o.paymentStatus}</TableCell>
              <TableCell>
                <Select size="small" value={o.status} onChange={(e) => setStatus({ id: o.id, status: e.target.value })}>
                  {STATUSES.map((s) => <MenuItem key={s} value={s}>{s}</MenuItem>)}
                </Select>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </Paper>
  );
}

export default function Admin() {
  const [tab, setTab] = useState(0);
  return (
    <>
      <Typography variant="h5" sx={{ mb: 2 }}>Admin dashboard</Typography>
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 3 }}>
        <Tab label="Overview" /><Tab label="Products" /><Tab label="Orders" />
      </Tabs>
      {tab === 0 && <Dashboard />}
      {tab === 1 && <Products />}
      {tab === 2 && <AdminOrders />}
    </>
  );
}
