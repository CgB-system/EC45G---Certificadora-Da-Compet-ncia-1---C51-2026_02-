import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Send, CheckCircle2, AlertCircle } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Input } from '../../components/ui/Input';
import { useToast } from '../../components/ui/Toast';
import { emailService, formularioService } from '../../services/formularioService';
import { getErrorMessage } from '../../services/api';
import type { ResultadoEnvioDTO } from '../../types/api';

export const CriarFormularioPage: React.FC = () => {
  const navigate = useNavigate();
  const { success, error: toastError } = useToast();

  const [titulo, setTitulo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [linkFormulario, setLinkFormulario] = useState('');
  const [assunto, setAssunto] = useState('');
  const [mensagem, setMensagem] = useState('');
  const [destinatariosTexto, setDestinatariosTexto] = useState('');

  const [enviando, setEnviando] = useState(false);
  const [resultados, setResultados] = useState<ResultadoEnvioDTO[]>([]);

  const handleDisparar = async (e: React.FormEvent) => {
    e.preventDefault();

    const emails = destinatariosTexto
      .split('\n')
      .map((s) => s.trim())
      .filter((s) => s.includes('@'));

    if (emails.length === 0) {
      toastError('Informe ao menos um e-mail válido para envio.');
      return;
    }

    setEnviando(true);
    setResultados([]);

    try {
      if (titulo.trim()) {
        await formularioService.criar({ titulo, descricao });
      }

      const res = await emailService.enviarFormulario({
        destinatarios: emails,
        assunto,
        mensagem,
        linkFormulario,
      });

      setResultados(res || []);
      success(`Formulário disparado com sucesso para ${emails.length} destinatários!`);
    } catch (err) {
      toastError(getErrorMessage(err));
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="space-y-6 max-w-5xl mx-auto pb-16 animate-in fade-in duration-200">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <Button
          variant="outline"
          size="sm"
          onClick={() => navigate('/formularios')}
          className="rounded-full px-4 text-xs font-sub"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          Voltar para Formulários
        </Button>
      </div>

      <div>
        <h1 className="text-3xl font-extrabold text-white tracking-tight">
          Criar e Disparar Formulário UTFPR
        </h1>
        <p className="text-sm text-[#9CA3AF] mt-1 font-sub">
          Cadastre as informações do Google Forms e envie o link por e-mail em lote para os estudantes
        </p>
      </div>

      <form onSubmit={handleDisparar} className="space-y-6">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-start">
          {/* Card 1: Informações do Formulário */}
          <Card className="p-6 bg-[#181919] border-[#262828] space-y-5">
            <h2 className="text-lg font-bold text-white tracking-tight border-b border-[#242626] pb-3">
              Informações do Formulário
            </h2>

            <div className="space-y-2">
              <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                Título do Processo Seletivo
              </label>
              <Input
                value={titulo}
                onChange={(e) => setTitulo(e.target.value)}
                placeholder="Ex: Processo Seletivo TEDI 2026.2"
                required
              />
            </div>

            <div className="space-y-2">
              <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                Link do Google Forms (URL Pública de Resposta)
              </label>
              <Input
                value={linkFormulario}
                onChange={(e) => setLinkFormulario(e.target.value)}
                placeholder="https://docs.google.com/forms/d/e/.../viewform"
                required
              />
            </div>

            <div className="space-y-2">
              <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                Descrição e Orientações
              </label>
              <textarea
                value={descricao}
                onChange={(e) => setDescricao(e.target.value)}
                rows={4}
                placeholder="Instruções para os candidatos..."
                className="w-full rounded-xl border border-[#333535] bg-[#171818] p-3 text-sm text-white placeholder:text-[#6e7272] focus:outline-none focus:border-[#71C3A5]"
              />
            </div>
          </Card>

          {/* Card 2: Disparo de E-mails */}
          <Card className="p-6 bg-[#181919] border-[#262828] space-y-5">
            <h2 className="text-lg font-bold text-white tracking-tight border-b border-[#242626] pb-3">
              Configuração do Disparo por E-mail
            </h2>

            <div className="space-y-2">
              <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                Assunto do E-mail
              </label>
              <Input
                value={assunto}
                onChange={(e) => setAssunto(e.target.value)}
                placeholder="Ex: [TEDI UTFPR] Processo Seletivo para Voluntários"
                required
              />
            </div>

            <div className="space-y-2">
              <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                Lista de E-mails dos Alunos (um por linha, máx 200)
              </label>
              <textarea
                value={destinatariosTexto}
                onChange={(e) => setDestinatariosTexto(e.target.value)}
                rows={5}
                placeholder="aluno1@alunos.utfpr.edu.br&#10;aluno2@alunos.utfpr.edu.br"
                className="w-full rounded-xl border border-[#333535] bg-[#171818] p-3 text-sm font-mono text-white placeholder:text-[#6e7272] focus:outline-none focus:border-[#71C3A5]"
                required
              />
            </div>

            <div className="space-y-2">
              <label className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                Corpo da Mensagem
              </label>
              <textarea
                value={mensagem}
                onChange={(e) => setMensagem(e.target.value)}
                rows={3}
                placeholder="Mensagem do corpo do e-mail acompanhando o link do forms..."
                className="w-full rounded-xl border border-[#333535] bg-[#171818] p-3 text-sm text-white focus:outline-none focus:border-[#71C3A5]"
              />
            </div>
          </Card>
        </div>

        {/* Botão de Envio */}
        <div className="flex justify-end pt-2">
          <Button
            type="submit"
            disabled={enviando}
            className="rounded-full px-8 py-3.5 font-bold text-sm font-sub"
          >
            <Send className="w-4 h-4 mr-2" />
            {enviando ? 'Enviando Disparos...' : 'Disparar Formulário por E-mail'}
          </Button>
        </div>
      </form>

      {/* Tabela de Resultados do Envio */}
      {resultados.length > 0 && (
        <Card className="p-6 bg-[#181919] border-[#262828] space-y-4">
          <h3 className="font-bold text-white text-base">Relatório de Envio de E-mails</h3>
          <div className="space-y-2">
            {resultados.map((res, i) => (
              <div
                key={i}
                className="flex items-center justify-between p-3 rounded-xl bg-[#202222] border border-[#2c2e2e] text-sm"
              >
                <span className="font-mono text-white">{res.destinatario}</span>
                {res.enviado ? (
                  <span className="flex items-center gap-1.5 text-xs text-[#71C3A5] font-semibold font-sub">
                    <CheckCircle2 className="w-4 h-4" /> Enviado
                  </span>
                ) : (
                  <span className="flex items-center gap-1.5 text-xs text-red-400 font-semibold font-sub">
                    <AlertCircle className="w-4 h-4" /> Falha: {res.erro}
                  </span>
                )}
              </div>
            ))}
          </div>
        </Card>
      )}
    </div>
  );
};
