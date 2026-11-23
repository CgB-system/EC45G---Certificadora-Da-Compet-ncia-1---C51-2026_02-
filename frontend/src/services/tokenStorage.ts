const ACCESS_TOKEN_KEY = 'tedi_access_token';
const REFRESH_TOKEN_KEY = 'tedi_refresh_token';
const PERSIST_PREF_KEY = 'tedi_persist_session';

export const tokenStorage = {
  getAccessToken(): string | null {
    return (
      localStorage.getItem(ACCESS_TOKEN_KEY) ||
      sessionStorage.getItem(ACCESS_TOKEN_KEY)
    );
  },

  getRefreshToken(): string | null {
    return (
      localStorage.getItem(REFRESH_TOKEN_KEY) ||
      sessionStorage.getItem(REFRESH_TOKEN_KEY)
    );
  },

  isPersistent(): boolean {
    if (localStorage.getItem(PERSIST_PREF_KEY) === 'true') return true;
    if (localStorage.getItem(REFRESH_TOKEN_KEY)) return true;
    return false;
  },

  setTokens(accessToken: string, refreshToken: string, lembrar?: boolean): void {
    const useLocal = lembrar !== undefined ? lembrar : this.isPersistent();

    this.clearTokens();

    const targetStorage = useLocal ? localStorage : sessionStorage;
    targetStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    targetStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);

    if (useLocal) {
      localStorage.setItem(PERSIST_PREF_KEY, 'true');
    }
  },

  clearTokens(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(PERSIST_PREF_KEY);
    sessionStorage.removeItem(ACCESS_TOKEN_KEY);
    sessionStorage.removeItem(REFRESH_TOKEN_KEY);
  },
};
