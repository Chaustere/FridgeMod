package fr.chaustere.fridgemod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FridgeMod.MODID)
public class FridgeMod {
    public static final String MODID = "fridgemod";

    public FridgeMod() {

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final ClientSideOnlyModEventRegistrar clientSideOnlyModEventRegistrar = new ClientSideOnlyModEventRegistrar(modEventBus);

        registerCommonEvents(modEventBus);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> clientSideOnlyModEventRegistrar::registerClientOnlyEvents);

    }

    public void registerCommonEvents(IEventBus eventBus) {
        eventBus.register(StartupCommon.class);
    }
}
