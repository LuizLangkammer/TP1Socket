package enums;

public enum Action {

        CONNECT((byte)1), CONFIRMCONNECTION((byte)10);

        private final byte valor;

        Action(byte valor){
                this.valor=valor;
        }

        public byte getValor() {
                return valor;
        }
}
