import { Card } from '../../components/ui/Card';
import { Heart, Award } from 'lucide-react';

export const SobrePage: React.FC = () => {
  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-16 animate-in fade-in duration-200">
      <div>
        <h1 className="text-3xl font-extrabold text-white tracking-tight">Sobre o Projeto TEDI</h1>
        <p className="text-sm text-[#9CA3AF] mt-1">
          Tecnologia e Educação Digital para a Terceira Idade — UTFPR Campus Cornélio Procópio
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card className="p-7 bg-[#181919] border-[#262828] space-y-4">
          <div className="w-10 h-10 rounded-xl bg-[#153428] text-[#71C3A5] flex items-center justify-center">
            <Heart className="w-5 h-5" />
          </div>
          <h2 className="text-xl font-bold text-white">Missão do Projeto</h2>
          <p className="text-sm text-[#CECECE] leading-relaxed">
            O TEDI promove a inclusão digital do público idoso através de minicursos, oficinas e
            especializações em informática prática ministrados por alunos de graduação da UTFPR.
          </p>
        </Card>

        <Card className="p-7 bg-[#181919] border-[#262828] space-y-4">
          <div className="w-10 h-10 rounded-xl bg-[#153428] text-[#71C3A5] flex items-center justify-center">
            <Award className="w-5 h-5" />
          </div>
          <h2 className="text-xl font-bold text-white">Certificadora da Competência 1</h2>
          <p className="text-sm text-[#CECECE] leading-relaxed">
            Desenvolvido na disciplina <strong>EC45G (C51 - 2026_02)</strong> do curso de
            Engenharia de Computação da UTFPR - Campus Cornélio Procópio.
          </p>
        </Card>
      </div>

      {/* Integrantes da Equipe */}
      <Card className="p-7 bg-[#181919] border-[#262828] space-y-5">
        <h2 className="text-lg font-bold text-white tracking-tight border-b border-[#242626] pb-3">
          Equipe de Desenvolvimento
        </h2>

        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
          {[
            { nome: 'Antonio Gabriel Nunes Martins', ra: '2865548', papel: 'Desenvolvedor' },
            { nome: 'Carlos Gabriel Baratieri', ra: '2706598', papel: 'Desenvolvedor' },
            { nome: 'Gabriel Rodrigues Pereira de Jesus', ra: '2706628', papel: 'Desenvolvedor' },
            { nome: 'Gustavo de Oliveira Gracionali', ra: '2618052', papel: 'Desenvolvedor' },
            { nome: 'Vitor Zacarias Carvalho Casagrande', ra: '2706717', papel: 'Desenvolvedor' },
          ].map((m, i) => (
            <div key={i} className="p-4 rounded-xl bg-[#202222] border border-[#2b2d2d] space-y-1">
              <div className="font-bold text-white text-sm">{m.nome}</div>
              <div className="text-xs text-[#71C3A5] font-mono">RA: {m.ra}</div>
              <div className="text-xs text-[#8E9090]">{m.papel}</div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
};
