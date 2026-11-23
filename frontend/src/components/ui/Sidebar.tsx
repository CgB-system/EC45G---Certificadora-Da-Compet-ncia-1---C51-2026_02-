import React from 'react';
import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import {
  Users,
  Clock,
  FileText,
  Info,
  UserPlus,
  UsersRound,
  Star,
  SlidersHorizontal,
  FilePlus2,
  ClipboardList,
  FileX2,
  LogOut,
} from 'lucide-react';
import logoImg from '../../assets/logo.png';
import { cn } from '../../lib/utils';
import { useAuth } from '../../contexts/AuthContext';
import { useToast } from './Toast';

export const Sidebar: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { usuario, logout } = useAuth();
  const { info } = useToast();

  const isActive = (path: string) => {
    if (path === '/' && location.pathname === '/') return true;
    if (path !== '/' && location.pathname.startsWith(path)) return true;
    return false;
  };

  const navItemClass = (path: string) =>
    cn(
      'flex items-center gap-3 px-3.5 py-2 rounded-xl text-sm font-medium transition-all duration-150',
      isActive(path)
        ? 'bg-[#71C3A5] text-[#0f0f0f] font-semibold shadow-sm'
        : 'text-[#9fa3a3] hover:text-white hover:bg-[#202222]'
    );

  const nomeExibicao = usuario
    ? `${usuario.nome}${usuario.sobrenome ? ` ${usuario.sobrenome.charAt(0)}.` : ''}`
    : 'Administrador';

  const iniciais = usuario
    ? `${usuario.nome.charAt(0)}${usuario.sobrenome ? usuario.sobrenome.charAt(0) : ''}`.toUpperCase()
    : 'AD';

  const handleLogout = async () => {
    await logout();
    info('Sessão encerrada com sucesso.');
    navigate('/login', { replace: true });
  };

  return (
    <aside className="w-64 h-screen bg-[#141515] border-r border-[#242626] flex flex-col justify-between select-none flex-shrink-0">
      {/* Top Header & Navigation */}
      <div className="flex flex-col overflow-y-auto">
        {/* Brand / Logo oficial TEDI */}
        <div className="px-6 py-6 border-b border-[#202222] flex items-center justify-center">
          <NavLink to="/" className="w-full flex items-center justify-center">
            <img
              src={logoImg}
              alt="TEDI — Gestão de Voluntários"
              className="w-full max-w-[200px] h-auto object-contain cursor-pointer transition-opacity hover:opacity-90"
            />
          </NavLink>
        </div>

        {/* Navigation Sections */}
        <div className="px-4 py-4 space-y-6">
          {/* Section: OVERVIEW */}
          <div>
            <div className="px-3 pb-2 text-[11px] font-semibold tracking-wider text-[#6b6e6e] uppercase">
              Overview
            </div>
            <nav className="space-y-1">
              <NavLink to="/voluntarios" className={navItemClass('/voluntarios')}>
                <Users className="w-4 h-4 flex-shrink-0" />
                <span>Voluntários</span>
              </NavLink>
              <NavLink to="/dashboards/resumo" className={navItemClass('/dashboards')}>
                <Clock className="w-4 h-4 flex-shrink-0" />
                <span>Dashboards</span>
              </NavLink>
              <NavLink to="/formularios" className={navItemClass('/formularios')}>
                <FileText className="w-4 h-4 flex-shrink-0" />
                <span>Formulários</span>
              </NavLink>
              <NavLink to="/sobre" className={navItemClass('/sobre')}>
                <Info className="w-4 h-4 flex-shrink-0" />
                <span>Sobre este projeto</span>
              </NavLink>
            </nav>
          </div>

          {/* Section: VOLUNTÁRIOS */}
          <div>
            <div className="px-3 pb-2 text-[11px] font-semibold tracking-wider text-[#6b6e6e] uppercase">
              Voluntários
            </div>
            <nav className="space-y-1">
              <NavLink to="/voluntarios/novo" className={navItemClass('/voluntarios/novo')}>
                <UserPlus className="w-4 h-4 flex-shrink-0" />
                <span>Adicionar novo Voluntário</span>
              </NavLink>
              <NavLink to="/voluntarios/gerenciar" className={navItemClass('/voluntarios/gerenciar')}>
                <UsersRound className="w-4 h-4 flex-shrink-0" />
                <span>Gerenciar Voluntários</span>
              </NavLink>
            </nav>
          </div>

          {/* Section: DASHBOARDS */}
          <div>
            <div className="px-3 pb-2 text-[11px] font-semibold tracking-wider text-[#6b6e6e] uppercase">
              Dashboards
            </div>
            <nav className="space-y-1">
              <NavLink to="/dashboards/resumo" className={navItemClass('/dashboards/resumo')}>
                <Star className="w-4 h-4 flex-shrink-0" />
                <span>Dashboard Resumido</span>
              </NavLink>
              <NavLink to="/dashboards/consultar" className={navItemClass('/dashboards/consultar')}>
                <SlidersHorizontal className="w-4 h-4 flex-shrink-0" />
                <span>Consultar Dashboard</span>
              </NavLink>
            </nav>
          </div>

          {/* Section: FORMULÁRIOS */}
          <div>
            <div className="px-3 pb-2 text-[11px] font-semibold tracking-wider text-[#6b6e6e] uppercase">
              Formulários
            </div>
            <nav className="space-y-1">
              <NavLink to="/formularios/novo" className={navItemClass('/formularios/novo')}>
                <FilePlus2 className="w-4 h-4 flex-shrink-0" />
                <span>Criar Novo Formulário</span>
              </NavLink>
              <NavLink to="/formularios/respostas" className={navItemClass('/formularios/respostas')}>
                <ClipboardList className="w-4 h-4 flex-shrink-0" />
                <span>Consultar Respostas</span>
              </NavLink>
              <NavLink to="/formularios/encerrar" className={navItemClass('/formularios/encerrar')}>
                <FileX2 className="w-4 h-4 flex-shrink-0" />
                <span>Encerrar Formulários</span>
              </NavLink>
            </nav>
          </div>
        </div>
      </div>

      {/* User Profile Card & Logout (Bottom of Sidebar) */}
      <div className="p-4 border-t border-[#202222]">
        <div className="flex items-center justify-between gap-2 p-2 rounded-2xl bg-[#71C3A5] text-[#0f0f0f] shadow-sm">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-9 h-9 rounded-full bg-[#0f0f0f] text-[#71C3A5] flex items-center justify-center font-bold text-xs overflow-hidden flex-shrink-0 border-2 border-[#5cb394]">
              {iniciais}
            </div>
            <div className="min-w-0 flex-1">
              <div className="text-sm font-bold truncate leading-tight text-[#0f0f0f]">
                {nomeExibicao}
              </div>
              <div className="text-[11px] font-medium text-[#18392d] truncate font-sub">
                {usuario?.tipo === 'ADMIN' ? 'Administrador' : 'Coordenador'}
              </div>
            </div>
          </div>

          <button
            type="button"
            onClick={handleLogout}
            title="Encerrar sessão"
            className="p-2 rounded-xl text-[#0f0f0f]/80 hover:text-[#0f0f0f] hover:bg-[#0f0f0f]/10 transition-colors cursor-pointer flex-shrink-0"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
