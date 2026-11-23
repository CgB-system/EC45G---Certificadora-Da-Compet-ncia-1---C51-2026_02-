import * as React from 'react';
import { cn } from '../../lib/utils';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'default' | 'outline' | 'ghost' | 'secondary' | 'danger';
  size?: 'sm' | 'md' | 'lg' | 'icon';
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant = 'default', size = 'md', ...props }, ref) => {
    const base = 'inline-flex items-center justify-center font-medium rounded-xl transition-all duration-150 focus:outline-none focus:ring-2 focus:ring-[#71C3A5] focus:ring-offset-2 focus:ring-offset-[#0f0f0f] disabled:opacity-50 disabled:pointer-events-none cursor-pointer';

    const variants = {
      default: 'bg-[#71C3A5] text-[#0f0f0f] hover:bg-[#5eb093] font-semibold shadow-sm active:scale-[0.98]',
      outline: 'border border-[#383b3b] bg-transparent text-[#e5e7eb] hover:bg-[#202222] hover:border-[#484b4b] active:scale-[0.98]',
      secondary: 'bg-[#252727] text-[#e5e7eb] hover:bg-[#303333] border border-[#343636] active:scale-[0.98]',
      ghost: 'bg-transparent text-[#CECECE] hover:text-white hover:bg-[#252727]',
      danger: 'bg-red-500/15 text-red-400 border border-red-500/30 hover:bg-red-500/25',
    };

    const sizes = {
      sm: 'text-xs px-3 py-1.5 gap-1.5',
      md: 'text-sm px-4 py-2.5 gap-2',
      lg: 'text-base px-6 py-3 gap-2.5',
      icon: 'p-2 w-9 h-9',
    };

    return (
      <button
        ref={ref}
        className={cn(base, variants[variant], sizes[size], className)}
        {...props}
      />
    );
  }
);
Button.displayName = 'Button';
