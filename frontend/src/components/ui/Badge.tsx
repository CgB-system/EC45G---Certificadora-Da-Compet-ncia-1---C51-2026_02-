import { cn } from '../../lib/utils';

export interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: 'ativo' | 'inativo' | 'pendente' | 'convertida' | 'descartada' | 'default';
}

export function Badge({ className, variant = 'default', children, ...props }: BadgeProps) {
  const isAtivo = variant === 'ativo';
  const isInativo = variant === 'inativo';
  const isPendente = variant === 'pendente';
  const isConvertida = variant === 'convertida';
  const isDescartada = variant === 'descartada';

  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium',
        isAtivo && 'bg-[#153428] text-[#71C3A5]',
        isInativo && 'bg-[#262828] text-[#9CA3AF]',
        isPendente && 'bg-[#3b2d18] text-[#eab308]',
        isConvertida && 'bg-[#153428] text-[#71C3A5]',
        isDescartada && 'bg-[#361a1a] text-[#f87171]',
        variant === 'default' && 'bg-[#272929] text-[#e5e7eb]',
        className
      )}
      {...props}
    >
      <span
        className={cn(
          'w-1.5 h-1.5 rounded-full',
          isAtivo && 'bg-[#71C3A5]',
          isInativo && 'bg-[#6B7280]',
          isPendente && 'bg-[#eab308]',
          isConvertida && 'bg-[#71C3A5]',
          isDescartada && 'bg-[#f87171]',
          variant === 'default' && 'bg-white'
        )}
      />
      {children}
    </span>
  );
}
