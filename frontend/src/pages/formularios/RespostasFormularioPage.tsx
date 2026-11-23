import React, { useEffect, useState } from 'react';
import {
  Download,
  XCircle,
  UserCheck,
  Search,
  Eye,
  X,
  FileQuestion,
} from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Input, Select } from '../../components/ui/Input';
import { Badge } from '../../components/ui/Badge';
import { useToast } from '../../components/ui/Toast';
import { formularioService } from '../../services/formularioService';
import { getErrorMessage } from '../../services/api';
import type { RespostaResponseDTO, FormularioDTO } from '../../types/api';

export const RespostasFormularioPage: React.FC = () => {
  const { success, error: toastError, info } = useToast();

  const [formularios, setFormularios] = useState<FormularioDTO[]>([]);
  const [formularioSelecionadoId, setFormularioSelecionadoId] = useState<string>('');
  const [respostas, setRespostas] = useState<RespostaResponseDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [importando, setImportando] = useState(false);
  const [filtroStatus, setFiltroStatus] = useState<string>('TODOS');
  const [busca, setBusca] = useState('');
  const [respostaVisualizada, setRespostaVisualizada] = useState<RespostaResponseDTO | null>(null);

  const carregarRespostas = async (formId: string) => {
    if (!formId) return;
    setLoading(true);
    try {
      const data = await formularioService.listarRespostas(formId);
      setRespostas(data || []);
    } catch {
      setRespostas([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    formularioService
      .listar()
      .then((forms) => {
        if (forms && forms.length > 0) {
          setFormularios(forms);
          setFormularioSelecionadoId(forms[0].id);
          carregarRespostas(forms[0].id);
        } else {
          setFormularios([]);
        }
      })
      .catch(() => setFormularios([]));
  }, []);

  const handleTrocarFormulario = (id: string) => {
    setFormularioSelecionadoId(id);
    carregarRespostas(id);
  };

  const handleImportarRespostas = async () => {
    if (!formularioSelecionadoId) {
      toastError('Nenhum formulário ativo selecionado para importação.');
      return;
    }
    setImportando(true);
    try {
      const res = await formularioService.importarRespostas(formularioSelecionadoId);
      success(`${res?.total ?? 0} respostas importadas e sincronizadas do Google Forms!`);
      carregarRespostas(formularioSelecionadoId);
    } catch (err) {
      toastError(getErrorMessage(err));
    } finally {
      setImportando(false);
    }
  };

  const handleConverter = async (resp: RespostaResponseDTO) => {
    try {
      await formularioService.converterEmVoluntario(resp.formularioId, resp.id);
      success('Candidato aprovado e convertido com sucesso em Voluntário!');
      setRespostas((prev) =>
        prev.map((r) => (r.id === resp.id ? { ...r, status: 'CONVERTIDA' } : r))
      );
      setRespostaVisualizada(null);
    } catch (err) {
      toastError(getErrorMessage(err));
    }
  };

  const handleDescartar = async (resp: RespostaResponseDTO) => {
    try {
      await formularioService.descartarResposta(resp.formularioId, resp.id);
      info('Inscrição marcada como descartada.');
      setRespostas((prev) =>
        prev.map((r) => (r.id === resp.id ? { ...r, status: 'DESCARTADA' } : r))
      );
      setRespostaVisualizada(null);
    } catch (err) {
      toastError(getErrorMessage(err));
    }
  };

  const respostasFiltradas = respostas.filter((r) => {
    const nome = r.respostas ? Object.values(r.respostas).join(' ') : '';
    const bateBusca = nome.toLowerCase().includes(busca.toLowerCase());
    const bateStatus = filtroStatus === 'TODOS' || r.status === filtroStatus;
    return bateBusca && bateStatus;
  });

  const totalPendentes = respostas.filter((r) => r.status === 'PENDENTE').length;
  const totalConvertidas = respostas.filter((r) => r.status === 'CONVERTIDA').length;
  const totalDescartadas = respostas.filter((r) => r.status === 'DESCARTADA').length;

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-16 animate-in fade-in duration-200">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-extrabold text-white tracking-tight">
            Consultar Respostas do Google Forms
          </h1>
          <p className="text-sm text-[#9CA3AF] mt-1 font-medium font-sub">
            Fichas e respostas de candidatos para validação e conversão em voluntários do TEDI
          </p>
        </div>

        <Button
          onClick={handleImportarRespostas}
          disabled={importando || !formularioSelecionadoId}
          className="rounded-full px-5 font-bold font-sub"
        >
          <Download className="w-4 h-4 mr-1.5" />
          {importando ? 'Importando...' : 'Importar Respostas do Forms'}
        </Button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="p-5 bg-[#181919] border-[#262828]">
          <span className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            Total Recebido
          </span>
          <div className="mt-2 text-3xl font-black text-white">{respostas.length}</div>
          <span className="text-[11px] text-[#9CA3AF] font-sub">Respostas cadastradas</span>
        </Card>

        <Card className="p-5 bg-[#181919] border-[#262828]">
          <span className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            Aguardando Avaliação
          </span>
          <div className="mt-2 text-3xl font-black text-[#eab308]">{totalPendentes}</div>
          <span className="text-[11px] text-[#eab308] font-sub">Fichas pendentes</span>
        </Card>

        <Card className="p-5 bg-[#181919] border-[#262828]">
          <span className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            Convertidos em Voluntários
          </span>
          <div className="mt-2 text-3xl font-black text-[#71C3A5]">{totalConvertidas}</div>
          <span className="text-[11px] text-[#71C3A5] font-sub">Aprovados no TEDI</span>
        </Card>

        <Card className="p-5 bg-[#181919] border-[#262828]">
          <span className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            Descartados
          </span>
          <div className="mt-2 text-3xl font-black text-red-400">{totalDescartadas}</div>
          <span className="text-[11px] text-[#9CA3AF] font-sub">Incompatíveis</span>
        </Card>
      </div>

      {/* Seletor de Formulário e Filtros */}
      <div className="flex flex-wrap items-center gap-4">
        {formularios.length > 0 && (
          <div className="w-72">
            <Select
              value={formularioSelecionadoId}
              onChange={(e) => handleTrocarFormulario(e.target.value)}
            >
              {formularios.map((f) => (
                <option key={f.id} value={f.id}>
                  {f.titulo}
                </option>
              ))}
            </Select>
          </div>
        )}

        <div className="flex-1 min-w-[240px]">
          <Input
            placeholder="Buscar por candidato ou resposta..."
            icon={<Search className="w-4 h-4" />}
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
          />
        </div>

        <div className="w-48">
          <Select value={filtroStatus} onChange={(e) => setFiltroStatus(e.target.value)}>
            <option value="TODOS">Status: Todos</option>
            <option value="PENDENTE">Pendentes</option>
            <option value="CONVERTIDA">Convertidos</option>
            <option value="DESCARTADA">Descartados</option>
          </Select>
        </div>
      </div>

      {/* Lista de Respostas */}
      <div className="space-y-3">
        <div className="grid grid-cols-12 px-5 py-2 text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
          <span className="col-span-4">Candidato / Identificação</span>
          <span className="col-span-3">Resumo das Respostas</span>
          <span className="col-span-2">Data de Envio</span>
          <span className="col-span-1">Status</span>
          <span className="col-span-2 text-right">Ações</span>
        </div>

        <div className="space-y-2">
          {respostasFiltradas.map((r) => {
            const respostasEntries = Object.entries(r.respostas || {});
            const primeiroCampo = respostasEntries[0] ? `${respostasEntries[0][1]}` : r.id;
            const segundoCampo = respostasEntries[1] ? `${respostasEntries[1][0]}: ${respostasEntries[1][1]}` : '—';
            const dataStr = r.criadaEm ? new Date(r.criadaEm).toLocaleDateString('pt-BR') : '—';

            return (
              <div
                key={r.id}
                className="grid grid-cols-12 items-center px-5 py-4 rounded-2xl bg-[#181919] border border-[#262828] hover:border-[#383a3a] transition-colors"
              >
                <div className="col-span-4">
                  <div className="font-semibold text-white">{primeiroCampo}</div>
                  <div className="text-xs text-[#8E9090] font-mono">ID: {r.id}</div>
                </div>

                <div className="col-span-3 text-sm text-[#CECECE] truncate pr-2 font-sub">
                  {segundoCampo}
                </div>

                <div className="col-span-2 text-sm text-[#8E9090] font-sub">{dataStr}</div>

                <div className="col-span-1">
                  <Badge
                    variant={
                      r.status === 'CONVERTIDA'
                        ? 'convertida'
                        : r.status === 'DESCARTADA'
                        ? 'descartada'
                        : 'pendente'
                    }
                  >
                    {r.status === 'CONVERTIDA'
                      ? 'Convertido'
                      : r.status === 'DESCARTADA'
                      ? 'Descartado'
                      : 'Pendente'}
                  </Badge>
                </div>

                <div className="col-span-2 flex items-center justify-end gap-2">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => setRespostaVisualizada(r)}
                    className="text-xs rounded-xl font-sub"
                  >
                    <Eye className="w-3.5 h-3.5 mr-1" /> Ficha
                  </Button>

                  {r.status === 'PENDENTE' && (
                    <Button
                      size="sm"
                      onClick={() => handleConverter(r)}
                      className="text-xs rounded-xl font-bold bg-[#71C3A5] text-[#0f0f0f] font-sub"
                    >
                      <UserCheck className="w-3.5 h-3.5 mr-1" /> Aprovar
                    </Button>
                  )}
                </div>
              </div>
            );
          })}

          {respostasFiltradas.length === 0 && !loading && (
            <Card className="p-12 text-center text-[#8E9090] bg-[#181919] border-[#262828] space-y-3">
              <FileQuestion className="w-8 h-8 text-[#8E9090] mx-auto opacity-70" />
              <div className="text-sm font-medium text-white font-sub">
                Nenhuma resposta de formulário registrada ainda no sistema.
              </div>
              <p className="text-xs text-[#8E9090] max-w-md mx-auto font-sub">
                Após os candidatos preencherem o Google Forms, clique no botão "Importar Respostas do Forms" para sincronizar os dados com o banco.
              </p>
            </Card>
          )}
        </div>
      </div>

      {/* Modal / Ficha de Resposta do Candidato */}
      {respostaVisualizada && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-in fade-in duration-150">
          <div className="relative w-full max-w-2xl bg-[#181919] border border-[#333535] rounded-3xl p-8 shadow-2xl space-y-6 max-h-[90vh] overflow-y-auto">
            {/* Header do Modal */}
            <div className="flex items-start justify-between border-b border-[#262828] pb-4">
              <div>
                <h3 className="text-2xl font-bold text-white tracking-tight">
                  Ficha de Resposta do Candidato
                </h3>
                <p className="text-xs text-[#8E9090] mt-1 font-mono">
                  ID: {respostaVisualizada.id} • Formulário: {respostaVisualizada.formularioId}
                </p>
              </div>
              <button
                onClick={() => setRespostaVisualizada(null)}
                className="text-[#8E9090] hover:text-white p-2 rounded-xl hover:bg-[#242626] cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Conteúdo das Perguntas e Respostas reais */}
            <div className="space-y-4">
              {Object.entries(respostaVisualizada.respostas || {}).map(([pergunta, resposta], i) => (
                <div key={i} className="p-4 rounded-xl bg-[#202222] border border-[#2b2d2d] space-y-1">
                  <div className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                    {pergunta}
                  </div>
                  <div className="text-sm text-white font-medium whitespace-pre-wrap">
                    {resposta || '—'}
                  </div>
                </div>
              ))}
            </div>

            {/* Ações do Professor / Avaliador */}
            <div className="flex items-center justify-between pt-4 border-t border-[#262828]">
              {respostaVisualizada.status === 'PENDENTE' ? (
                <>
                  <Button
                    variant="danger"
                    size="sm"
                    onClick={() => handleDescartar(respostaVisualizada)}
                    className="rounded-full px-5 font-sub"
                  >
                    <XCircle className="w-4 h-4 mr-1.5" />
                    Descartar Candidato
                  </Button>

                  <Button
                    onClick={() => handleConverter(respostaVisualizada)}
                    className="rounded-full px-6 py-2.5 font-bold font-sub"
                  >
                    <UserCheck className="w-4 h-4 mr-1.5" />
                    Aprovar e Converter em Voluntário
                  </Button>
                </>
              ) : (
                <div className="w-full flex items-center justify-between">
                  <span className="text-sm text-[#8E9090] font-sub">
                    Status atual:{' '}
                    <strong className="text-white">{respostaVisualizada.status}</strong>
                  </span>
                  <Button
                    variant="outline"
                    onClick={() => setRespostaVisualizada(null)}
                    className="rounded-full px-5 font-sub"
                  >
                    Fechar Ficha
                  </Button>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
