import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatRa(ra: string) {
  if (!ra) return '';
  return ra.toUpperCase();
}

export function formatPeriodo(periodo: number) {
  return `${periodo}º período`;
}
