package net.plazmix.protocollib.entity;

public interface FakeEntityMob extends FakeEntityLiving {

    void setAgressive(boolean agressive);

    void setNoAI(boolean noAI);

    boolean isAgressive();

    boolean isNoAI();
}
