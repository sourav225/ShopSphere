import { Alert, Button, Chip, Paper, Stack, Typography } from '@mui/material';
import { errMsg, useGetMyOrdersQuery, usePayOrderMutation } from '../api';

const payColor = { PAID: 'success', FAILED: 'error', PENDING: 'warning', REFUNDED: 'default' };

export default function Orders() {
  const { data: orders, isLoading } = useGetMyOrdersQuery();
  const [pay, { error }] = usePayOrderMutation();

  if (isLoading) return <Typography>Loading…</Typography>;
  if (!orders?.length) return <Alert severity="info">No orders yet.</Alert>;

  return (
    <Stack spacing={2}>
      <Typography variant="h5">My orders</Typography>
      {error && <Alert severity="error">{errMsg(error)}</Alert>}
      {orders.map((o) => (
        <Paper key={o.id} sx={{ p: 3 }}>
          <Stack direction="row" justifyContent="space-between" alignItems="center" flexWrap="wrap" gap={1}>
            <Typography variant="h6">Order #{o.id} · ${Number(o.total).toFixed(2)}</Typography>
            <Stack direction="row" spacing={1}>
              <Chip label={o.status} />
              <Chip label={`Payment: ${o.paymentStatus}`} color={payColor[o.paymentStatus]} />
            </Stack>
          </Stack>
          <Typography variant="caption" color="text.secondary">{new Date(o.createdAt).toLocaleString()}</Typography>
          {o.items.map((i) => (
            <Typography key={i.productId} variant="body2">{i.quantity} × {i.productName} @ ${Number(i.unitPrice).toFixed(2)}</Typography>
          ))}
          {o.paymentStatus !== 'PAID' && o.status !== 'CANCELLED' && (
            <Stack direction="row" spacing={1} sx={{ mt: 2 }}>
              <Button variant="contained" onClick={() => pay({ id: o.id, success: true })}>Pay now</Button>
              <Button color="error" onClick={() => pay({ id: o.id, success: false })}>Simulate failure</Button>
            </Stack>
          )}
        </Paper>
      ))}
    </Stack>
  );
}
