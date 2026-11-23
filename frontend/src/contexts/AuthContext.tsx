import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from 'react';
import type { UsuarioLoginDTO, UsuarioResponseDTO } from '../types/api';
import { authService } from '../services/authService';
import { tokenStorage } from '../services/tokenStorage';

interface AuthContextValue {
  usuario: UsuarioResponseDTO | null;
  autenticado: boolean;
  carregando: boolean;
  login: (
    credenciais: UsuarioLoginDTO,
    lembrar: boolean
  ) => Promise<UsuarioResponseDTO>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [usuario, setUsuario] = useState<UsuarioResponseDTO | null>(null);
  const [carregando, setCarregando] = useState<boolean>(true);

                                                
  useEffect(() => {
    let ativo = true;

    async function restaurarSessao() {
      const temToken =
        Boolean(tokenStorage.getAccessToken()) ||
        Boolean(tokenStorage.getRefreshToken());

      if (!temToken) {
        if (ativo) setCarregando(false);
        return;
      }

      try {
        const perfil = await authService.me();
        if (ativo) {
          setUsuario(perfil);
        }
      } catch {
        tokenStorage.clearTokens();
        if (ativo) {
          setUsuario(null);
        }
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    }

    restaurarSessao();

    return () => {
      ativo = false;
    };
  }, []);

                                                                          
  useEffect(() => {
    const handleUnauthorized = () => {
      tokenStorage.clearTokens();
      setUsuario(null);
    };

    window.addEventListener('tedi:unauthorized', handleUnauthorized);
    return () => {
      window.removeEventListener('tedi:unauthorized', handleUnauthorized);
    };
  }, []);

  const login = useCallback(
    async (
      credenciais: UsuarioLoginDTO,
      lembrar: boolean
    ): Promise<UsuarioResponseDTO> => {
      const tokens = await authService.login(credenciais);
      tokenStorage.setTokens(
        tokens.accessToken,
        tokens.refreshToken,
        lembrar
      );

      try {
        const perfil = await authService.me();
        if (perfil.tipo !== 'ADMINISTRADOR') {
          tokenStorage.clearTokens();
          throw new Error(
            'Acesso negado. Apenas administradores da coordenação do TEDI podem acessar a plataforma.'
          );
        }
        setUsuario(perfil);
        return perfil;
      } catch (err) {
        tokenStorage.clearTokens();
        setUsuario(null);
        throw err;
      }
    },
    []
  );

  const logout = useCallback(async () => {
    const refreshToken = tokenStorage.getRefreshToken();
    try {
      if (refreshToken) {
        await authService.logout(refreshToken);
      }
    } catch {
      // Ignora erro de rede no logout e garante limpeza local
    } finally {
      tokenStorage.clearTokens();
      setUsuario(null);
    }
  }, []);

  return (
    <AuthContext.Provider
      value={{
        usuario,
        autenticado: Boolean(usuario),
        carregando,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser utilizado dentro de um AuthProvider');
  }
  return context;
}
