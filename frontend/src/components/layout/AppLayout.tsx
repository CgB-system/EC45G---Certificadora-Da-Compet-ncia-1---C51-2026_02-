import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from '../ui/Sidebar';

export const AppLayout: React.FC = () => {
  return (
    <div className="flex w-screen h-screen overflow-hidden bg-[#0f0f0f] text-[#CECECE]">
      <Sidebar />
      <main className="flex-1 h-screen overflow-y-auto px-10 py-8">
        <Outlet />
      </main>
    </div>
  );
};
