import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

const baseQuery = fetchBaseQuery({
  baseUrl: '/api',
  prepareHeaders: (headers, { getState }) => {
    const token = getState().auth.token;
    if (token) headers.set('authorization', `Bearer ${token}`);
    return headers;
  },
});

export const api = createApi({
  reducerPath: 'api',
  baseQuery,
  tagTypes: ['Product', 'Cart', 'Order', 'Stats'],
  endpoints: (b) => ({
    login: b.mutation({ query: (body) => ({ url: '/auth/login', method: 'POST', body }) }),
    register: b.mutation({ query: (body) => ({ url: '/auth/register', method: 'POST', body }) }),

    getProducts: b.query({
      query: ({ q, category, page, size }) => ({
        url: '/products',
        params: { q: q || undefined, category: category || undefined, page, size },
      }),
      providesTags: ['Product'],
    }),
    createProduct: b.mutation({
      query: (body) => ({ url: '/products', method: 'POST', body }),
      invalidatesTags: ['Product', 'Stats'],
    }),
    updateProduct: b.mutation({
      query: ({ id, ...body }) => ({ url: `/products/${id}`, method: 'PUT', body }),
      invalidatesTags: ['Product'],
    }),
    deleteProduct: b.mutation({
      query: (id) => ({ url: `/products/${id}`, method: 'DELETE' }),
      invalidatesTags: ['Product', 'Stats'],
    }),

    getCart: b.query({ query: () => '/cart', providesTags: ['Cart'] }),
    addToCart: b.mutation({
      query: (body) => ({ url: '/cart', method: 'POST', body }),
      invalidatesTags: ['Cart'],
    }),
    removeFromCart: b.mutation({
      query: (productId) => ({ url: `/cart/${productId}`, method: 'DELETE' }),
      invalidatesTags: ['Cart'],
    }),

    placeOrder: b.mutation({
      query: () => ({ url: '/orders', method: 'POST' }),
      invalidatesTags: ['Cart', 'Order', 'Product', 'Stats'],
    }),
    getMyOrders: b.query({ query: () => '/orders', providesTags: ['Order'] }),
    payOrder: b.mutation({
      query: ({ id, success }) => ({ url: `/orders/${id}/pay`, method: 'POST', params: { success } }),
      invalidatesTags: ['Order', 'Stats'],
    }),

    getAdminOrders: b.query({ query: () => '/admin/orders', providesTags: ['Order'] }),
    updateOrderStatus: b.mutation({
      query: ({ id, status }) => ({ url: `/admin/orders/${id}/status`, method: 'PATCH', body: { status } }),
      invalidatesTags: ['Order', 'Stats'],
    }),
    getStats: b.query({ query: () => '/admin/stats', providesTags: ['Stats'] }),
  }),
});

export const {
  useLoginMutation, useRegisterMutation, useGetProductsQuery, useCreateProductMutation,
  useUpdateProductMutation, useDeleteProductMutation, useGetCartQuery, useAddToCartMutation,
  useRemoveFromCartMutation, usePlaceOrderMutation, useGetMyOrdersQuery, usePayOrderMutation,
  useGetAdminOrdersQuery, useUpdateOrderStatusMutation, useGetStatsQuery,
} = api;

export const errMsg = (e) => e?.data?.message || (e?.status ? `Request failed (${e.status})` : 'Something went wrong');
