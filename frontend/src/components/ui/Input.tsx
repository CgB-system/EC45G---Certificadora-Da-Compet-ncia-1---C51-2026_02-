import * as React from 'react';
import { cn } from '../../lib/utils';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  icon?: React.ReactNode;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, type, icon, ...props }, ref) => {
    if (icon) {
      return (
        <div className="relative flex items-center w-full">
          <div className="absolute left-3.5 text-[#8E9090] pointer-events-none flex items-center">
            {icon}
          </div>
          <input
            type={type}
            className={cn(
              'flex h-11 w-full rounded-xl border border-[#333535] bg-[#171818] pl-10 pr-4 py-2 text-sm text-white placeholder:text-[#6e7272] transition-colors focus:outline-none focus:border-[#71C3A5] focus:ring-1 focus:ring-[#71C3A5] disabled:cursor-not-allowed disabled:opacity-50',
              className
            )}
            ref={ref}
            {...props}
          />
        </div>
      );
    }

    return (
      <input
        type={type}
        className={cn(
          'flex h-11 w-full rounded-xl border border-[#333535] bg-[#171818] px-4 py-2 text-sm text-white placeholder:text-[#6e7272] transition-colors focus:outline-none focus:border-[#71C3A5] focus:ring-1 focus:ring-[#71C3A5] disabled:cursor-not-allowed disabled:opacity-50',
          className
        )}
        ref={ref}
        {...props}
      />
    );
  }
);
Input.displayName = 'Input';

export interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  options?: { value: string | number; label: string }[];
}

export const Select = React.forwardRef<HTMLSelectElement, SelectProps>(
  ({ className, children, options, ...props }, ref) => {
    return (
      <div className="relative w-full">
        <select
          ref={ref}
          className={cn(
            'flex h-11 w-full appearance-none rounded-xl border border-[#333535] bg-[#171818] px-4 py-2 pr-9 text-sm text-white transition-colors focus:outline-none focus:border-[#71C3A5] focus:ring-1 focus:ring-[#71C3A5] disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer',
            className
          )}
          {...props}
        >
          {options
            ? options.map((opt) => (
                <option key={opt.value} value={opt.value} className="bg-[#1c1d1d] text-white">
                  {opt.label}
                </option>
              ))
            : children}
        </select>
        <div className="pointer-events-none absolute right-3.5 top-1/2 -translate-y-1/2 text-[#8E9090]">
          <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20">
            <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
          </svg>
        </div>
      </div>
    );
  }
);
Select.displayName = 'Select';
