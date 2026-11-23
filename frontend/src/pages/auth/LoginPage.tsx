import React, { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, Lock, Mail, AlertCircle, ShieldCheck } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { useToast } from '../../components/ui/Toast';
import { useAuth } from '../../contexts/AuthContext';
import { getErrorMessage } from '../../services/api';
import logoImg from '../../assets/logo.png';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { autenticado, carregando, login } = useAuth();
  const { success } = useToast();

  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [lembrar, setLembrar] = useState(true);
  const [mostrarSenha, setMostrarSenha] = useState(false);
  const [submetendo, setSubmetendo] = useState(false);
  const [erroMensagem, setErroMensagem] = useState<string | null>(null);

  const destinoPosLogin =
    (location.state as { from?: { pathname?: string } } | null)?.from
      ?.pathname || '/';

  if (!carregando && autenticado) {
    return <Navigate to={destinoPosLogin} replace />;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErroMensagem(null);

    if (!email.trim() || !senha) {
      setErroMensagem('Preencha o e-mail institucional e a senha para entrar.');
      return;
    }

    setSubmetendo(true);
    try {
      const perfil = await login({ email: email.trim(), senha }, lembrar);
      success(`Bem-vindo(a), ${perfil.nome}!`);
      navigate(destinoPosLogin, { replace: true });
    } catch (err) {
      setErroMensagem(getErrorMessage(err));
    } finally {
      setSubmetendo(false);
    }
  };

  return (
    <div className="min-h-screen w-screen grid grid-cols-1 lg:grid-cols-12 bg-[#0f0f0f] text-[#CECECE] overflow-x-hidden">
      {/* Coluna Esquerda: Formulário de Autenticação */}
      <div className="lg:col-span-5 flex flex-col justify-center px-8 sm:px-14 xl:px-20 py-12 bg-[#0f0f0f] border-r border-[#1f2121]">
        <div className="w-full max-w-md mx-auto space-y-8 animate-in fade-in duration-200">
          {/* Logo visível no topo da coluna de formulário em telas menores */}
          <div className="lg:hidden pb-2">
            <img
              src={logoImg}
              alt="TEDI — Gestão de Voluntários"
              className="h-12 w-auto object-contain"
            />
          </div>

          {/* Cabeçalho inspirado em login.png */}
          <div className="space-y-3">
            <div className="flex items-center gap-3">
              <span className="w-7 h-[2px] bg-[#71C3A5] rounded-full" />
              <span className="text-[11px] font-bold tracking-[0.2em] uppercase text-[#71C3A5] font-sub">
                Bem-vindo de volta
              </span>
            </div>

            <h1 className="text-4xl sm:text-5xl font-black tracking-tight leading-[1.08]">
              <span className="block text-white">Entrar no</span>
              <span className="block text-[#71C3A5]">TEDI</span>
            </h1>

            <p className="text-sm text-[#9CA3AF] font-sub pt-1">
              Acesse sua conta para gerenciar voluntários e processos seletivos
            </p>
          </div>

          {/* Alerta de Erro */}
          {erroMensagem && (
            <div className="flex items-start gap-3 p-4 rounded-xl bg-[#2b1414] border border-[#572222] text-[#f87171] text-xs font-sub">
              <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5" />
              <span className="leading-relaxed">{erroMensagem}</span>
            </div>
          )}

          {/* Formulário */}
          <form onSubmit={handleSubmit} className="space-y-5">
            {/* E-mail */}
            <div className="space-y-2">
              <label
                htmlFor="login-email"
                className="block text-xs font-semibold text-white tracking-wide font-sub"
              >
                E-mail
              </label>
              <Input
                id="login-email"
                type="email"
                autoComplete="email"
                placeholder="seu@email.com"
                icon={<Mail className="w-4 h-4" />}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={submetendo}
                required
              />
            </div>

            {/* Senha */}
            <div className="space-y-2">
              <label
                htmlFor="login-senha"
                className="block text-xs font-semibold text-white tracking-wide font-sub"
              >
                Senha
              </label>
              <div className="relative flex items-center">
                <Input
                  id="login-senha"
                  type={mostrarSenha ? 'text' : 'password'}
                  autoComplete="current-password"
                  placeholder="••••••••"
                  icon={<Lock className="w-4 h-4" />}
                  value={senha}
                  onChange={(e) => setSenha(e.target.value)}
                  disabled={submetendo}
                  className="pr-11"
                  required
                />
                <button
                  type="button"
                  onClick={() => setMostrarSenha((v) => !v)}
                  tabIndex={-1}
                  className="absolute right-3.5 text-[#8E9090] hover:text-white transition-colors cursor-pointer"
                  aria-label={mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'}
                >
                  {mostrarSenha ? (
                    <EyeOff className="w-4 h-4" />
                  ) : (
                    <Eye className="w-4 h-4" />
                  )}
                </button>
              </div>
            </div>

            {/* Lembrar de mim */}
            <div className="flex items-center justify-between pt-1">
              <label className="inline-flex items-center gap-2.5 cursor-pointer select-none">
                <input
                  type="checkbox"
                  checked={lembrar}
                  onChange={(e) => setLembrar(e.target.checked)}
                  disabled={submetendo}
                  className="w-4 h-4 rounded border-[#383b3b] bg-[#171818] accent-[#71C3A5] cursor-pointer"
                />
                <span className="text-xs text-[#9CA3AF] font-sub">
                  Lembrar de mim
                </span>
              </label>
            </div>

            {/* Botão Entrar */}
            <div className="pt-2">
              <Button
                type="submit"
                size="lg"
                disabled={submetendo}
                className="w-full py-3.5 text-sm font-bold tracking-wide"
              >
                {submetendo ? 'Autenticando...' : 'Entrar'}
              </Button>
            </div>
          </form>

          {/* Rodapé Restrito */}
          <div className="pt-4 border-t border-[#1e2020] flex items-center justify-center gap-2 text-xs text-[#8E9090] font-sub">
            <ShieldCheck className="w-4 h-4 text-[#71C3A5] flex-shrink-0" />
            <span>Acesso restrito à coordenação do TEDI</span>
          </div>
        </div>
      </div>

      {/* Coluna Direita: Painel Institucional da Marca TEDI */}
      <div className="hidden lg:flex lg:col-span-7 relative bg-[#71C3A5] text-[#0f0f0f] flex-col justify-between p-14 xl:p-20 overflow-hidden select-none">
        {/* Elementos decorativos de fundo sutis */}
        <div className="pointer-events-none absolute -top-32 -right-32 w-[480px] h-[480px] rounded-full bg-white/12 blur-3xl" />
        <div className="pointer-events-none absolute -bottom-40 -left-24 w-[420px] h-[420px] rounded-full bg-[#0f0f0f]/10 blur-3xl" />

        {/* Topo: Logo oficial do TEDI em container escuro de alto contraste */}
        <div className="relative z-10 flex items-center justify-between">
          <div className="inline-flex items-center px-6 py-4 rounded-2xl bg-[#0f0f0f] border border-[#242626] shadow-2xl">
            <img
              src={logoImg}
              alt="TEDI — Gestão de Voluntários"
              className="h-14 w-auto object-contain"
            />
          </div>

          <span className="px-4 py-1.5 rounded-full bg-[#0f0f0f]/10 border border-[#0f0f0f]/15 text-[11px] font-bold tracking-wider uppercase text-[#0f0f0f] font-sub">
            UTFPR • Cornélio Procópio
          </span>
        </div>

        {/* Centro: Manchete Principal inspirada em login.png */}
        <div className="relative z-10 max-w-2xl space-y-6 my-auto py-12">
          <div className="flex items-center gap-3">
            <span className="w-8 h-[2px] bg-[#0f0f0f] rounded-full" />
            <span className="text-xs font-extrabold tracking-[0.22em] uppercase text-[#0f0f0f]/80 font-sub">
              Extensão Universitária • Inclusão Digital
            </span>
          </div>

          <h2 className="text-5xl xl:text-6xl font-black tracking-tight leading-[1.06] text-[#0f0f0f]">
            Tecnologia que aproxima{' '}
            <span className="inline-block px-3 py-0.5 rounded-2xl bg-[#0f0f0f] text-[#71C3A5] shadow-lg">
              gerações
            </span>
          </h2>

          <p className="text-base xl:text-lg text-[#0f0f0f]/80 font-medium leading-relaxed max-w-xl font-sub">
            Gerencie a equipe de voluntários, conduza processos seletivos e
            acompanhe os indicadores acadêmicos do projeto TEDI em um único
            ambiente integrado.
          </p>
        </div>

        {/* Rodapé institucional do painel */}
        <div className="relative z-10 flex items-center justify-between text-xs font-semibold text-[#0f0f0f]/70 border-t border-[#0f0f0f]/15 pt-6 font-sub">
          <span>TEDI — Terceira Idade Digital</span>
          <span>EC45G • Certificadora da Competência 1</span>
        </div>
      </div>
    </div>
  );
};
