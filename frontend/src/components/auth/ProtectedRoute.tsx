import React from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

export const ProtectedRoute: React.FC = () => {
  const { autenticado, carregando } = useAuth();
  const location = useLocation();

  if (carregando) {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen bg-[#0f0f0f] text-[#CECECE] gap-4 select-none">
        <div className="w-10 h-10 rounded-full border-3 border-[#262828] border-t-[#71C3A5] animate-spin" />
        <span className="text-xs font-semibold tracking-widest uppercase text-[#8E9090] font-sub">
          Validando sessão...
        </span>
      </div>
    );
  }

  if (!autenticado) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
};
