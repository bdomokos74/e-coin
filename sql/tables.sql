CREATE TABLE IF NOT EXISTS WALLET
(
    ID          INTEGER NOT NULL,
    PRIVATE_KEY BLOB(2k)    NOT NULL,
    PUBLIC_KEY  BLOB(2k)    NOT NULL,
    PRIMARY KEY (ID)
);

create sequence BLOCKCHAIN_SEQ;

CREATE TABLE IF NOT EXISTS BLOCKCHAIN
(
    ID            INTEGER not null,
    PREVIOUS_HASH BLOB(2k)    NOT NULL,
    CURRENT_HASH  BLOB(2k)    NOT NULL,
    LEDGER_ID     INTEGER NOT NULL UNIQUE,
    CREATED_ON    TEXT,
    CREATED_BY    BLOB(2k),
    MINING_POINTS TEXT,
    LUCK          NUMERIC,
    PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS TRANSACTIONS
(
    ID         INTEGER NOT NULL,
    FROM_W     BLOB(2k),
    TO_W       BLOB(2k),
    LEDGER_ID  INTEGER,
    VALUE_V    INTEGER,
    SIGNATURE  BLOB    NOT NULL,
    CREATED_ON TEXT,
    PRIMARY KEY (ID)
);

