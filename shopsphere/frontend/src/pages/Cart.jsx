import { useNavigate } from 'react-router-dom';
import {
  Alert, Button, IconButton, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, Typography,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { errMsg, useGetCartQuery, usePlaceOrderMutation, useRemoveFromCartMutation } from '../api';

export default function Cart() {
  const { data: cart, isLoading } = useGetCartQuery();
  const [remove] = useRemoveFromCartMutation();
  const [placeOrder, { isLoading: placing, error }] = usePlaceOrderMutation();
  const navigate = useNavigate();

  if (isLoading) return <Typography>Loading…</Typography>;
  if (!cart?.items.length) return <Alert severity="info">Your cart is empty.</Alert>;

  const checkout = async () => {
    try { await placeOrder().unwrap(); navigate('/orders'); } catch { /* shown below */ }
  };

  return (
    <Paper sx={{ p: 3 }}>
      <Typography variant="h5" sx={{ mb: 2 }}>Your cart</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{errMsg(error)}</Alert>}
      <Table>
        <TableHead>
          <TableRow><TableCell>Product</TableCell><TableCell align="right">Price</TableCell><TableCell align="right">Qty</TableCell><TableCell align="right">Subtotal</TableCell><TableCell /></TableRow>
        </TableHead>
        <TableBody>
          {cart.items.map((i) => (
            <TableRow key={i.productId}>
              <TableCell>{i.name}</TableCell>
              <TableCell align="right">${Number(i.price).toFixed(2)}</TableCell>
              <TableCell align="right">{i.quantity}</TableCell>
              <TableCell align="right">${Number(i.subtotal).toFixed(2)}</TableCell>
              <TableCell align="right"><IconButton onClick={() => remove(i.productId)}><DeleteIcon /></IconButton></TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mt: 3 }}>
        <Typography variant="h6">Total: ${Number(cart.total).toFixed(2)}</Typography>
        <Button variant="contained" size="large" disabled={placing} onClick={checkout}>Place order</Button>
      </Stack>
    </Paper>
  );
}
