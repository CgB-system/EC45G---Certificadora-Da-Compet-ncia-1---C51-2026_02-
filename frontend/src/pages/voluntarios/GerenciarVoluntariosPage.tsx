import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Input, Select } from '../../components/ui/Input';
import { Badge } from '../../components/ui/Badge';
import { useToast } from '../../components/ui/Toast';
import { voluntarioService } from '../../services/voluntarioService';
import { cursoService } from '../../services/auxiliaresService';
import { getErrorMessage } from '../../services/api';
import { formatPeriodo } from '../../lib/utils';
import type {
  VoluntarioResponseDTO,
  CursoResponseDTO,
  StatusVoluntario,
} from '../../types/api';

export const GerenciarVoluntariosPage: React.FC = () => {
  const navigate = useNavigate();
  const { success, error: toastError } = useToast();

  const [loading, setLoading] = useState(true);
  const [voluntarios, setVoluntarios] = useState<VoluntarioResponseDTO[]>([]);
  const [cursos, setCursos] = useState<CursoResponseDTO[]>([]);
  const [selectedVoluntario, setSelectedVoluntario] = useState<VoluntarioResponseDTO | null>(null);

  // Filtros
  const [busca, setBusca] = useState('');
  const [cursoFiltro, setCursoFiltro] = useState<string>('');
  const [periodoFiltro, setPeriodoFiltro] = useState<string>('');
  const [statusFiltro, setStatusFiltro] = useState<string>('TODOS');

  // Paginação
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(1);
  const [totalElementos, setTotalElementos] = useState(0);

  // Estados de edição do painel lateral
  const [editNome, setEditNome] = useState('');
  const [editCursoId, setEditCursoId] = useState<number>(0);
  const [editPeriodo, setEditPeriodo] = useState<number>(1);
  const [editStatus, setEditStatus] = useState<StatusVoluntario>('ATIVO');
  const [salvando, setSalvando] = useState(false);

  const selecionarVoluntario = (vol: VoluntarioResponseDTO) => {
    setSelectedVoluntario(vol);
    setEditNome(`${vol.nome} ${vol.sobrenome || ''}`.trim());
    setEditCursoId(vol.curso?.id || (cursos[0]?.id ?? 0));
    setEditPeriodo(vol.periodo || 1);
    setEditStatus(vol.status);
  };

  const carregarCursos = async () => {
    try {
      const data = await cursoService.listar();
      setCursos(data || []);
    } catch {
      setCursos([]);
    }
  };

  const carregarVoluntarios = async () => {
    setLoading(true);
    try {
      const params: Record<string, unknown> = {
        page: paginaAtual,
        size: 10,
        sort: 'id,asc',
      };
      if (busca.trim()) params.nome = busca.trim();
      if (cursoFiltro) params.cursoId = Number(cursoFiltro);
      if (periodoFiltro) params.periodo = Number(periodoFiltro);
      if (statusFiltro !== 'TODOS') params.status = statusFiltro;

      const res = await voluntarioService.listar(params);
      if (res?.conteudo) {
        setVoluntarios(res.conteudo);
        setTotalPaginas(res.totalPaginas || 1);
        setTotalElementos(res.totalElementos || 0);

        if (res.conteudo.length > 0 && !selectedVoluntario) {
          selecionarVoluntario(res.conteudo[0]);
        }
      } else {
        setVoluntarios([]);
      }
    } catch (err) {
      setVoluntarios([]);
      setTotalPaginas(1);
      setTotalElementos(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarCursos();
  }, []);

  useEffect(() => {
    carregarVoluntarios();
  }, [paginaAtual, cursoFiltro, periodoFiltro, statusFiltro]);

  const handleBuscar = (e: React.FormEvent) => {
    e.preventDefault();
    setPaginaAtual(0);
    carregarVoluntarios();
  };

  const handleSalvarEdicao = async () => {
    if (!selectedVoluntario) return;
    setSalvando(true);
    try {
      const partesNome = editNome.trim().split(' ');
      const primeiroNome = partesNome[0] || selectedVoluntario.nome;
      const sobrenome = partesNome.slice(1).join(' ') || selectedVoluntario.sobrenome || '';

      await voluntarioService.atualizar(selectedVoluntario.id, {
        nome: primeiroNome,
        sobrenome: sobrenome,
        email: selectedVoluntario.email,
        ra: selectedVoluntario.ra,
        periodo: Number(editPeriodo),
        coeficiente: selectedVoluntario.coeficiente,
        cursoId: Number(editCursoId),
        experiencia: selectedVoluntario.experiencia ?? undefined,
      });

      if (editStatus !== selectedVoluntario.status) {
        await voluntarioService.alterarStatus(selectedVoluntario.id, editStatus);
      }

      success(`Alterações salvas com sucesso!`);
      carregarVoluntarios();
    } catch (err) {
      toastError(getErrorMessage(err));
    } finally {
      setSalvando(false);
    }
  };

  const ativosCount = voluntarios.filter((v) => v.status === 'ATIVO').length;
  const inativosCount = voluntarios.filter((v) => v.status === 'INATIVO').length;

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-12 animate-in fade-in duration-200">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-extrabold text-white tracking-tight">Gerenciar Voluntários</h1>
          <p className="text-sm text-[#9CA3AF] mt-1 font-medium font-sub">
            {ativosCount} ativos • {inativosCount} inativos
          </p>
        </div>

        <Button onClick={() => navigate('/voluntarios/novo')} className="rounded-full px-5 font-bold">
          <Plus className="w-4 h-4" />
          Adicionar Voluntário
        </Button>
      </div>

      {/* Filter & Search Bar */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Search Input */}
        <form onSubmit={handleBuscar} className="flex-1 min-w-[280px]">
          <Input
            placeholder="Buscar por nome ou RA..."
            icon={<Search className="w-4 h-4" />}
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
          />
        </form>

        {/* Curso Filter */}
        <div className="w-44">
          <Select
            value={cursoFiltro}
            onChange={(e) => {
              setCursoFiltro(e.target.value);
              setPaginaAtual(0);
            }}
          >
            <option value="">Curso: Todos</option>
            {cursos.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nome}
              </option>
            ))}
          </Select>
        </div>

        {/* Período Filter */}
        <div className="w-36">
          <Select
            value={periodoFiltro}
            onChange={(e) => {
              setPeriodoFiltro(e.target.value);
              setPaginaAtual(0);
            }}
          >
            <option value="">Período: Todos</option>
            {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((p) => (
              <option key={p} value={p}>
                {p}º período
              </option>
            ))}
          </Select>
        </div>

        {/* Status Filter */}
        <div className="w-36">
          <Select
            value={statusFiltro}
            onChange={(e) => {
              setStatusFiltro(e.target.value);
              setPaginaAtual(0);
            }}
          >
            <option value="TODOS">Status: Todos</option>
            <option value="ATIVO">Ativo</option>
            <option value="INATIVO">Inativo</option>
          </Select>
        </div>
      </div>

      {/* Main Grid: Listagem (8 cols) + Painel de Edição Lateral (4 cols) */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* Tabela de Voluntários */}
        <div className="lg:col-span-8 space-y-3">
          {/* Header das Colunas */}
          <div className="grid grid-cols-12 px-5 py-2 text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            <span className="col-span-4">Voluntário</span>
            <span className="col-span-3">Curso</span>
            <span className="col-span-2">Período</span>
            <span className="col-span-2">Semestre</span>
            <span className="col-span-1 text-right">Status</span>
          </div>

          {/* Linhas da Tabela */}
          <div className="space-y-2">
            {voluntarios.map((vol) => {
              const isSelected = selectedVoluntario?.id === vol.id;
              return (
                <div
                  key={vol.id}
                  onClick={() => selecionarVoluntario(vol)}
                  className={`grid grid-cols-12 items-center px-5 py-4 rounded-2xl bg-[#181919] border transition-all duration-150 cursor-pointer ${
                    isSelected
                      ? 'border-[#71C3A5] bg-[#1a201e] shadow-md'
                      : 'border-[#262828] hover:border-[#383b3b] hover:bg-[#1f2121]'
                  }`}
                >
                  {/* Nome & RA */}
                  <div className="col-span-4">
                    <div className="font-semibold text-white">
                      {vol.nome} {vol.sobrenome}
                    </div>
                    <div className="text-xs text-[#8E9090] font-mono">{vol.ra}</div>
                  </div>

                  {/* Curso */}
                  <div className="col-span-3 text-sm text-[#CECECE] truncate pr-2">
                    {vol.curso?.nome || '—'}
                  </div>

                  {/* Período */}
                  <div className="col-span-2 text-sm text-[#CECECE] font-sub">
                    {formatPeriodo(vol.periodo)}
                  </div>

                  {/* Semestre de Ingresso / Experiência */}
                  <div className="col-span-2 text-sm text-[#8E9090] font-sub truncate">
                    {vol.experiencia?.replace('Semestre de ingresso: ', '') || '2026.2'}
                  </div>

                  {/* Status */}
                  <div className="col-span-1 text-right">
                    <Badge variant={vol.status === 'ATIVO' ? 'ativo' : 'inativo'}>
                      {vol.status === 'ATIVO' ? 'Ativo' : 'Inativo'}
                    </Badge>
                  </div>
                </div>
              );
            })}

            {voluntarios.length === 0 && !loading && (
              <div className="p-12 text-center text-[#8E9090] bg-[#181919] rounded-2xl border border-[#262828] font-sub">
                Nenhum voluntário cadastrado no banco de dados.
              </div>
            )}
          </div>

          {/* Paginação */}
          {totalPaginas > 1 && (
            <div className="flex items-center justify-between pt-4 px-2 text-sm text-[#8E9090] font-sub">
              <span>
                Total de {totalElementos} voluntários ({paginaAtual + 1} de {totalPaginas})
              </span>
              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  disabled={paginaAtual === 0}
                  onClick={() => setPaginaAtual((p) => Math.max(p - 1, 0))}
                >
                  <ChevronLeft className="w-4 h-4" /> Anterior
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  disabled={paginaAtual >= totalPaginas - 1}
                  onClick={() => setPaginaAtual((p) => p + 1)}
                >
                  Próxima <ChevronRight className="w-4 h-4" />
                </Button>
              </div>
            </div>
          )}
        </div>

        {/* Side Panel: Editar Voluntário */}
        <div className="lg:col-span-4 sticky top-6">
          <Card className="p-6 bg-[#181919] border-[#262828]">
            <h2 className="text-lg font-bold text-white tracking-tight mb-5">Editar Voluntário</h2>

            {selectedVoluntario ? (
              <div className="space-y-5">
                {/* Nome */}
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                    Nome Completo
                  </label>
                  <Input
                    value={editNome}
                    onChange={(e) => setEditNome(e.target.value)}
                    placeholder="Nome do voluntário"
                  />
                </div>

                {/* Curso */}
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                    Curso
                  </label>
                  <Select
                    value={editCursoId}
                    onChange={(e) => setEditCursoId(Number(e.target.value))}
                  >
                    {cursos.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.nome}
                      </option>
                    ))}
                  </Select>
                </div>

                {/* Período Atual */}
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                    Período Atual
                  </label>
                  <Select
                    value={editPeriodo}
                    onChange={(e) => setEditPeriodo(Number(e.target.value))}
                  >
                    {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((p) => (
                      <option key={p} value={p}>
                        {p}º período
                      </option>
                    ))}
                  </Select>
                </div>

                {/* Status Toggle / Select */}
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                    Status
                  </label>
                  <div className="grid grid-cols-2 gap-2">
                    <button
                      type="button"
                      onClick={() => setEditStatus('ATIVO')}
                      className={`flex items-center justify-center gap-2 py-2.5 rounded-xl text-sm font-semibold transition-colors cursor-pointer font-sub ${
                        editStatus === 'ATIVO'
                          ? 'bg-[#153428] text-[#71C3A5] border border-[#235841]'
                          : 'bg-[#202222] text-[#8E9090] border border-[#2b2d2d]'
                      }`}
                    >
                      <span className="w-2 h-2 rounded-full bg-[#71C3A5]" />
                      Ativo
                    </button>
                    <button
                      type="button"
                      onClick={() => setEditStatus('INATIVO')}
                      className={`flex items-center justify-center gap-2 py-2.5 rounded-xl text-sm font-semibold transition-colors cursor-pointer font-sub ${
                        editStatus === 'INATIVO'
                          ? 'bg-[#2d2020] text-[#f87171] border border-[#502828]'
                          : 'bg-[#202222] text-[#8E9090] border border-[#2b2d2d]'
                      }`}
                    >
                      <span className="w-2 h-2 rounded-full bg-[#6B7280]" />
                      Inativo
                    </button>
                  </div>
                </div>

                {/* Botão de Salvar */}
                <div className="pt-2">
                  <Button
                    onClick={handleSalvarEdicao}
                    disabled={salvando}
                    className="w-full py-3 text-sm font-bold font-sub"
                  >
                    {salvando ? 'Salvando...' : 'Salvar Alterações'}
                  </Button>
                </div>
              </div>
            ) : (
              <div className="py-8 text-center text-sm text-[#8E9090] font-sub">
                Selecione um voluntário na lista para editar seus dados.
              </div>
            )}
          </Card>
        </div>
      </div>
    </div>
  );
};
