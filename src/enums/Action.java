package enums;

public enum Action {

        CONNECT((byte)1), CONFIRMCONNECTION((byte)2),
        YOURTURN((byte)3),
        OPEN((byte) 4),
        HIT((byte)5), NOTHIT((byte)6),
        WON((byte)7);

        private final byte value;

        Action(byte value){
                this.value=value;
        }

        public byte getValue() {
                return value;
        }
}
