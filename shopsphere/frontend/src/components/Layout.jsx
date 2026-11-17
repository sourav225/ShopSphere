import { Link as RouterLink, Outlet, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { AppBar, Badge, Box, Button, Container, IconButton, Toolbar, Typography } from '@mui/material';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import StorefrontIcon from '@mui/icons-material/Storefront';
import { logout } from '../authSlice';
import { api, useGetCartQuery } from '../api';

export default function Layout() {
  const user = useSelector((s) => s.auth.user);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { data: cart } = useGetCartQuery(undefined, { skip: !user });
  const count = cart?.items?.reduce((n, i) => n + i.quantity, 0) || 0;

  const onLogout = () => {
    dispatch(logout());
    dispatch(api.util.resetApiState());
    navigate('/');
  };

  return (
    <Box>
      <AppBar position="sticky" elevation={0}>
        <Toolbar>
          <StorefrontIcon sx={{ mr: 1 }} />
          <Typography variant="h6" component={RouterLink} to="/" sx={{ color: 'inherit', textDecoration: 'none', flexGrow: 1 }}>
            ShopSphere
          </Typography>
          {user?.role === 'ADMIN' && <Button color="inherit" component={RouterLink} to="/admin">Admin</Button>}
          {user && <Button color="inherit" component={RouterLink} to="/orders">Orders</Button>}
          {user && (
            <IconButton color="inherit" component={RouterLink} to="/cart" aria-label="cart">
              <Badge badgeContent={count} color="secondary"><ShoppingCartIcon /></Badge>
            </IconButton>
          )}
          {user
            ? <Button color="inherit" onClick={onLogout}>Logout ({user.name})</Button>
            : <Button color="inherit" component={RouterLink} to="/login">Login</Button>}
        </Toolbar>
      </AppBar>
      <Container sx={{ py: 4 }}><Outlet /></Container>
    </Box>
  );
}
