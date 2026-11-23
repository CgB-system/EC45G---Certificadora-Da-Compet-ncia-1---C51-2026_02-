import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Lock, Check, AlertCircle } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Input, Select } from '../../components/ui/Input';
import { useToast } from '../../components/ui/Toast';
import { voluntarioService } from '../../services/voluntarioService';
import { cursoService, habilidadeService } from '../../services/auxiliaresService';
import { formularioService } from '../../services/formularioService';
import { getErrorMessage } from '../../services/api';
import type {
  CursoResponseDTO,
  HabilidadeResponseDTO,
  StatusVoluntario,
  RespostaResponseDTO,
} from '../../types/api';

export const CadastrarVoluntarioPage: React.FC = () => {
  const navigate = useNavigate();
  const { success, error: toastError } = useToast();

  const [cursos, setCursos] = useState<CursoResponseDTO[]>([]);
  const [habilidades, setHabilidades] = useState<HabilidadeResponseDTO[]>([]);
  const [candidatosFormulario, setCandidatosFormulario] = useState<RespostaResponseDTO[]>([]);
  const [origemCandidatoId, setOrigemCandidatoId] = useState<string>('manual');

                                                         
  const [nome, setNome] = useState('');
  const [sobrenome, setSobrenome] = useState('');
  const [ra, setRa] = useState('');
  const [email, setEmail] = useState('');
  const [cursoId, setCursoId] = useState<number>(0);
  const [periodo, setPeriodo] = useState<number>(1);
  const [semestreIngresso, setSemestreIngresso] = useState('2026.2');
  const [statusInicial, setStatusInicial] = useState<StatusVoluntario>('ATIVO');
  const [coeficiente, setCoeficiente] = useState('');
  const [habilidadesSelecionadas, setHabilidadesSelecionadas] = useState<number[]>([]);
  const [telefone, setTelefone] = useState('');
  const [isCandidatoBloqueado, setIsCandidatoBloqueado] = useState(false);
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
                                             
    cursoService
      .listar()
      .then((data) => {
        if (data && data.length > 0) {
          setCursos(data);
          setCursoId(data[0].id);
        }
      })
      .catch(() => setCursos([]));

                                                                   
    habilidadeService
      .listar()
      .then((data) => {
        if (data && data.length > 0) {
          setHabilidades(data);
        }
      })
      .catch(() => setHabilidades([]));

                                                                                 
    formularioService
      .listar()
      .then(async (forms) => {
        if (forms && forms.length > 0) {
          const respostas = await formularioService.listarRespostas(forms[0].id).catch(() => []);
          const pendentes = respostas.filter((r) => r.status === 'PENDENTE');
          setCandidatosFormulario(pendentes);
        }
      })
      .catch(() => setCandidatosFormulario([]));
  }, []);

  const handleSelecionarOrigem = (valor: string) => {
    setOrigemCandidatoId(valor);
    if (valor === 'manual') {
      setIsCandidatoBloqueado(false);
      setNome('');
      setSobrenome('');
      setRa('');
      setEmail('');
      return;
    }

    const cand = candidatosFormulario.find((c) => c.id === valor);
    if (cand) {
      setIsCandidatoBloqueado(true);
      const nomeCompleto = cand.respostas['Nome Completo'] || '';
      const partes = nomeCompleto.trim().split(' ');
      setNome(partes[0] || '');
      setSobrenome(partes.slice(1).join(' ') || '');
      setRa(cand.respostas['RA (Registro Acadêmico)'] || '');
      setEmail(cand.respostas['E-mail UTFPR'] || '');
    }
  };

  const toggleHabilidade = (id: number) => {
    setHabilidadesSelecionadas((prev) =>
      prev.includes(id) ? prev.filter((h) => h !== id) : [...prev, id]
    );
  };

  const validarCampos = () => {
    if (!nome.trim()) {
      toastError('O nome do voluntário é obrigatório.');
      return false;
    }
    if (!ra.trim() || ra.trim().length !== 7) {
      toastError('O RA deve conter exatamente 7 caracteres alfanuméricos.');
      return false;
    }
    if (!email.trim() || !email.includes('@')) {
      toastError('Informe um e-mail institucional válido.');
      return false;
    }
    if (!cursoId) {
      toastError('Selecione um curso válido cadastrado no sistema.');
      return false;
    }
    return true;
  };

  const salvar = async (cadastrarOutro = false) => {
    if (!validarCampos()) return;
    setSalvando(true);

    try {
      const payload = {
        nome: nome.trim(),
        sobrenome: sobrenome.trim() || 'Sobrenome',
        email: email.trim(),
        ra: ra.trim().toUpperCase(),
        periodo: Number(periodo),
        coeficiente: parseFloat(coeficiente) || 7.0,
        cursoId: Number(cursoId),
        status: statusInicial,
        experiencia: `Semestre de ingresso: ${semestreIngresso}`,
        habilidadesIds: habilidadesSelecionadas.length > 0 ? habilidadesSelecionadas : undefined,
        telefones: telefone.trim()
          ? [
              {
                codigo: '+55',
                ddd: telefone.slice(0, 2) || '43',
                numero: telefone.slice(2) || '999999999',
              },
            ]
          : undefined,
      };

      await voluntarioService.cadastrar(payload);
      success(`Voluntário ${nome} cadastrado com sucesso!`);

      if (cadastrarOutro) {
        setNome('');
        setSobrenome('');
        setRa('');
        setEmail('');
        setCoeficiente('');
        setTelefone('');
        setHabilidadesSelecionadas([]);
        setIsCandidatoBloqueado(false);
        setOrigemCandidatoId('manual');
      } else {
        navigate('/voluntarios/gerenciar');
      }
    } catch (err) {
      toastError(getErrorMessage(err));
    } finally {
      setSalvando(false);
    }
  };

  return (
    <div className="space-y-6 max-w-6xl mx-auto pb-16 animate-in fade-in duration-200">
                                            
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2 text-sm text-[#8E9090] font-sub">
          <span
            onClick={() => navigate('/voluntarios')}
            className="hover:text-white cursor-pointer transition-colors"
          >
            Voluntários
          </span>
          <span>&gt;</span>
          <span className="text-[#CECECE] font-medium">Cadastrar Novo Voluntário</span>
        </div>

        <Button
          variant="outline"
          size="sm"
          onClick={() => navigate('/voluntarios/gerenciar')}
          className="rounded-full px-4 text-xs font-sub"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          Voltar para lista
        </Button>
      </div>

                        
      <div>
        <h1 className="text-3xl font-extrabold text-white tracking-tight">
          Cadastrar Novo Voluntário
        </h1>
        <p className="text-sm text-[#9CA3AF] mt-1 font-sub">
          Cadastro de integrantes e sincronização com candidatos aprovados no seletivo
        </p>
      </div>

                                    
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 items-start mt-6">
                                                   
        <Card className="p-7 bg-[#181919] border-[#262828] space-y-6">
          <h2 className="text-lg font-bold text-white tracking-tight border-b border-[#242626] pb-4">
            Identificação do Aluno
          </h2>

                                               
          <div className="space-y-2">
            <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub flex items-center justify-between">
              <span>Buscar Aluno / Candidato (UTFPR)</span>
            </label>
            <Select
              value={origemCandidatoId}
              onChange={(e) => handleSelecionarOrigem(e.target.value)}
            >
              <option value="manual">Cadastro Manual / Direto</option>
              {candidatosFormulario.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.respostas['Nome Completo'] || 'Candidato'} (Inscrição Seletivo)
                </option>
              ))}
            </Select>
          </div>

                                  
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                Nome
              </label>
              <Input
                value={nome}
                onChange={(e) => setNome(e.target.value)}
                placeholder="Nome"
                disabled={isCandidatoBloqueado}
              />
            </div>
            <div className="space-y-2">
              <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                Sobrenome
              </label>
              <Input
                value={sobrenome}
                onChange={(e) => setSobrenome(e.target.value)}
                placeholder="Sobrenome"
                disabled={isCandidatoBloqueado}
              />
            </div>
          </div>

                    
          <div className="space-y-2">
            <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub flex items-center gap-1.5">
              <span>RA (Registro Acadêmico)</span>
              {isCandidatoBloqueado && (
                <span className="text-[10px] text-[#71C3A5] font-normal flex items-center gap-1 lowercase">
                  • auto-preenchido <Lock className="w-3 h-3" />
                </span>
              )}
            </label>
            <Input
              value={ra}
              onChange={(e) => setRa(e.target.value)}
              placeholder="Ex: 2614890 (7 caracteres)"
              disabled={isCandidatoBloqueado}
              className={isCandidatoBloqueado ? 'bg-[#1e2020] text-[#9CA3AF]' : ''}
              maxLength={7}
            />
          </div>

                                      
          <div className="space-y-2">
            <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub flex items-center gap-1.5">
              <span>E-mail Institucional UTFPR</span>
              {isCandidatoBloqueado && (
                <span className="text-[10px] text-[#71C3A5] font-normal flex items-center gap-1 lowercase">
                  • auto-preenchido <Lock className="w-3 h-3" />
                </span>
              )}
            </label>
            <Input
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="aluno@alunos.utfpr.edu.br"
              disabled={isCandidatoBloqueado}
              className={isCandidatoBloqueado ? 'bg-[#1e2020] text-[#9CA3AF]' : ''}
            />
          </div>

                          
          <div className="space-y-2">
            <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
              Telefone / WhatsApp (Opcional)
            </label>
            <Input
              value={telefone}
              onChange={(e) => setTelefone(e.target.value)}
              placeholder="Ex: 43991234567"
            />
          </div>
        </Card>

                                                         
        <Card className="p-7 bg-[#181919] border-[#262828] space-y-6">
          <h2 className="text-lg font-bold text-white tracking-tight border-b border-[#242626] pb-4">
            Vínculo Acadêmico & Projeto
          </h2>

                       
          <div className="space-y-2">
            <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
              Curso (cadastrado no banco)
            </label>
            {cursos.length > 0 ? (
              <Select
                value={cursoId}
                onChange={(e) => setCursoId(Number(e.target.value))}
              >
                {cursos.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.nome}
                  </option>
                ))}
              </Select>
            ) : (
              <div className="p-3 bg-[#202222] border border-[#2b2d2d] rounded-xl text-xs text-[#8E9090] font-sub">
                Nenhum curso cadastrado retornado pela API (/api/v1/cursos).
              </div>
            )}
          </div>

                               
          <div className="space-y-2">
            <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
              Período Atual
            </label>
            <Select
              value={periodo}
              onChange={(e) => setPeriodo(Number(e.target.value))}
            >
              {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((p) => (
                <option key={p} value={p}>
                  {p}º período
                </option>
              ))}
            </Select>
          </div>

                                              
          <div className="space-y-2">
            <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
              Semestre de Ingresso no TEDI
            </label>
            <Select
              value={semestreIngresso}
              onChange={(e) => setSemestreIngresso(e.target.value)}
            >
              <option value="2026.2">2026.2</option>
              <option value="2026.1">2026.1</option>
              <option value="2025.2">2025.2</option>
              <option value="2025.1">2025.1</option>
            </Select>
          </div>

                                
          <div className="space-y-2">
            <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
              Status Inicial
            </label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setStatusInicial('ATIVO')}
                className={`py-3 rounded-xl text-sm font-semibold transition-all cursor-pointer font-sub ${
                  statusInicial === 'ATIVO'
                    ? 'bg-[#71C3A5] text-[#0f0f0f] shadow-sm font-bold'
                    : 'bg-[#202222] text-[#8E9090] hover:text-white border border-[#2b2d2d]'
                }`}
              >
                Ativo
              </button>
              <button
                type="button"
                onClick={() => setStatusInicial('INATIVO')}
                className={`py-3 rounded-xl text-sm font-semibold transition-all cursor-pointer font-sub ${
                  statusInicial === 'INATIVO'
                    ? 'bg-[#71C3A5] text-[#0f0f0f] shadow-sm font-bold'
                    : 'bg-[#202222] text-[#8E9090] hover:text-white border border-[#2b2d2d]'
                }`}
              >
                Inativo
              </button>
            </div>
          </div>

                                           
          <div className="space-y-2">
            <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
              Coeficiente de Rendimento (opcional)
            </label>
            <Input
              value={coeficiente}
              onChange={(e) => setCoeficiente(e.target.value)}
              placeholder="Ex: 8.5000"
            />
          </div>

                                                          
          <div className="space-y-3 pt-2">
            <div className="flex items-center justify-between">
              <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                Habilidades Técnicas (Banco de Dados)
              </label>
              <span className="text-[11px] text-[#71C3A5] font-sub">
                {habilidadesSelecionadas.length} selecionadas
              </span>
            </div>

            {habilidades.length > 0 ? (
              <div className="flex flex-wrap gap-2">
                {habilidades.map((hab) => {
                  const checked = habilidadesSelecionadas.includes(hab.id);
                  return (
                    <button
                      key={hab.id}
                      type="button"
                      onClick={() => toggleHabilidade(hab.id)}
                      className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition-colors cursor-pointer font-sub ${
                        checked
                          ? 'bg-[#153428] text-[#71C3A5] border border-[#245842]'
                          : 'bg-[#202222] text-[#8E9090] border border-[#2c2e2e] hover:border-[#383b3b]'
                      }`}
                    >
                      {checked && <Check className="w-3.5 h-3.5 text-[#71C3A5]" />}
                      {hab.nome}
                    </button>
                  );
                })}
              </div>
            ) : (
              <div className="p-3.5 bg-[#202222] border border-[#2b2d2d] rounded-xl text-xs text-[#8E9090] font-sub flex items-center gap-2">
                <AlertCircle className="w-4 h-4 text-[#8E9090] flex-shrink-0" />
                <span>
                  Nenhuma habilidade cadastrada no banco de dados. Insira registros na tabela{' '}
                  <code className="text-[#71C3A5]">tbl_Habilidade</code> para exibi-las aqui.
                </span>
              </div>
            )}
          </div>
        </Card>
      </div>

                            
      <div className="flex items-center justify-end gap-4 pt-6">
        <Button
          variant="outline"
          onClick={() => salvar(true)}
          disabled={salvando}
          className="rounded-full px-6 py-3 font-semibold font-sub"
        >
          Salvar e cadastrar outro
        </Button>
        <Button
          onClick={() => salvar(false)}
          disabled={salvando}
          className="rounded-full px-8 py-3 font-bold font-sub"
        >
          {salvando ? 'Salvando...' : 'Salvar Voluntário'}
        </Button>
      </div>
    </div>
  );
};
