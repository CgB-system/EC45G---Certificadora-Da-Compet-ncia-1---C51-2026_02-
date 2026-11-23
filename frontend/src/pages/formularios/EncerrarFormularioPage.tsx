import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FileX2, CheckCircle, ArrowLeft, FileQuestion } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { useToast } from '../../components/ui/Toast';
import { formularioService } from '../../services/formularioService';
import { getErrorMessage } from '../../services/api';
import type { FormularioDTO } from '../../types/api';

export const EncerrarFormularioPage: React.FC = () => {
  const navigate = useNavigate();
  const { success, error: toastError } = useToast();

  const [formularios, setFormularios] = useState<FormularioDTO[]>([]);
  const [encerrados, setEncerrados] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    formularioService
      .listar()
      .then((data) => {
        setFormularios(data || []);
      })
      .catch(() => setFormularios([]))
      .finally(() => setLoading(false));
  }, []);

  const handleEncerrar = async (form: FormularioDTO) => {
    try {
      await formularioService.encerrar(form.id);
      setEncerrados((prev) => [...prev, form.id]);
      success(`Formulário "${form.titulo}" encerrado com sucesso.`);
    } catch (err) {
      toastError(getErrorMessage(err));
    }
  };

  return (
    <div className="space-y-6 max-w-5xl mx-auto pb-16 animate-in fade-in duration-200">
      <div className="flex items-center justify-between">
        <Button
          variant="outline"
          size="sm"
          onClick={() => navigate('/formularios')}
          className="rounded-full px-4 text-xs font-sub"
        >
          <ArrowLeft className="w-3.5 h-3.5" /> Voltar
        </Button>
      </div>

      <div>
        <h1 className="text-3xl font-extrabold text-white tracking-tight">
          Encerrar Processos Seletivos
        </h1>
        <p className="text-sm text-[#9CA3AF] mt-1 font-sub">
          Finalize o recebimento de novas respostas no Google Forms e encerre o processo
        </p>
      </div>

      <div className="space-y-4">
        {formularios.map((f) => {
          const isEncerrado = encerrados.includes(f.id);
          return (
            <Card key={f.id} className="p-6 bg-[#181919] border-[#262828] space-y-4">
              <div className="flex items-start justify-between">
                <div>
                  <div className="flex items-center gap-3">
                    <h3 className="text-lg font-bold text-white">{f.titulo}</h3>
                    <Badge variant={isEncerrado ? 'inativo' : 'ativo'}>
                      {isEncerrado ? 'Encerrado' : 'Ativo para Respostas'}
                    </Badge>
                  </div>
                  <p className="text-sm text-[#9CA3AF] mt-1">{f.descricao}</p>
                </div>

                <div>
                  {!isEncerrado ? (
                    <Button
                      variant="danger"
                      size="sm"
                      onClick={() => handleEncerrar(f)}
                      className="rounded-full px-5 text-xs font-semibold font-sub"
                    >
                      <FileX2 className="w-3.5 h-3.5 mr-1" />
                      Encerrar Formulário
                    </Button>
                  ) : (
                    <span className="text-xs text-[#8E9090] font-medium flex items-center gap-1 font-sub">
                      <CheckCircle className="w-4 h-4 text-[#71C3A5]" />
                      Finalizado
                    </span>
                  )}
                </div>
              </div>
            </Card>
          );
        })}

        {formularios.length === 0 && !loading && (
          <Card className="p-12 text-center text-[#8E9090] bg-[#181919] border-[#262828] space-y-3">
            <FileQuestion className="w-8 h-8 text-[#8E9090] mx-auto opacity-70" />
            <div className="text-sm font-medium text-white font-sub">
              Nenhum formulário cadastrado no banco de dados para encerramento.
            </div>
          </Card>
        )}
      </div>
    </div>
  );
};
