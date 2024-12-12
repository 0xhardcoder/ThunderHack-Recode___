package thunder.hack.features.modules.player;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.util.math.BlockPos;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.features.modules.Module;
import thunder.hack.utility.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.util.text.TextFormatting;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.client.MinecraftClient;

public class RWGodMode extends Module {
    private final Timer stopWatch = new Timer();
    private final Timer warpDelay = new Timer();
    private boolean clickingSlot13 = false;
    private boolean slot21Clicked = false;
    private boolean menuClosed = false;
    private Thread updateThread;

  public RWGodMode() {
        super("RWGodMode", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        resetState();
        sendWarpCommand();
        warpDelay.reset();
    }

    @Override
    public void onDisable() {
        resetState();
    }

    private void sendWarpCommand() {
        mc.player.sendChatMessage("/warp");
        mc.mouseHelper.grabMouse();
        menuClosed = false;
    }

    private void clickSlot(int slotIndex) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.openContainer != null && mc.player.openContainer.getSlot(slotIndex) != null) {
            mc.playerController.windowClick(mc.player.openContainer.windowId, slotIndex, 0, ClickType.QUICK_MOVE, mc.player);


        } else {

        }
    }

    private void forceCloseMenu() {
        Minecraft mc = Minecraft.getInstance();
        mc.displayGuiScreen(null);
        mc.mouseHelper.ungrabMouse();
        menuClosed = true;

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        sendMessage(TextFormatting.WHITE + "GodMod активирован!");
    }



    private void startClickingSlot13() {
        clickingSlot13 = true;

    }

    private void stopClickingSlot13() {
        clickingSlot13 = false;

    }

    private void resetState() {
        clickingSlot13 = false;
        slot21Clicked = false;
        menuClosed = false;
        stopWatch.reset();
        warpDelay.reset();

    }

    private BossOverlayGui getBossOverlayGui() {
        try {
            Minecraft mc = Minecraft.getInstance();
            return mc.ingameGUI.getBossOverlay();
        } catch (Exception e) {

            return null;
        }
    }

    private boolean isPvpBossBarActive() {
        BossOverlayGui bossOverlayGui = getBossOverlayGui();
        if (bossOverlayGui == null) {

            return false;
        }

        Map<?, ClientBossInfo> bossBars;

        try {

            Field bossInfosField = BossOverlayGui.class.getDeclaredField("mapBossInfos");
            bossInfosField.setAccessible(true);
            bossBars = (Map<?, ClientBossInfo>) bossInfosField.get(bossOverlayGui);

            for (ClientBossInfo bossInfo : bossBars.values()) {
                String bossName = bossInfo.getName().getString();

                if (bossName.contains("Режим ПВП") || bossName.contains("PVP")) {

                    return true;
                }
            }
        } catch (Exception e) {

        }


        return false;
    }


    @Override
    public void onUpdate() {
                if (!menuClosed && warpDelay.passedMs(1000)) {
                    forceCloseMenu();
                }

                if (warpDelay.passedMs(500) && !slot21Clicked) {
                    clickSlot(21);
                    slot21Clicked = true;
                }

                if (isPvpBossBarActive()) {
                    if (!clickingSlot13) {
                        startClickingSlot13();
                    }
                } else {
                    if (clickingSlot13) {
                        stopClickingSlot13();
                    }
                }

                if (clickingSlot13 && stopWatch.passedMs(5)) {
                    clickSlot(13);
                    stopWatch.reset();
                }
    }
  
  /*
    private Timer confirmTimer = new Timer();
    private boolean teleported;

    public RWGodMode() {
        super("RWGodMode", Category.PLAYER);
        confirmTimer.setMs(99999);
    }

    @EventHandler
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof TeleportConfirmC2SPacket && confirmTimer.getPassedTimeMs() < 5000) {
            teleported = true;
            e.cancel();
        }
    }

    @Override
    public void onDisable(){
        teleported = false;
    }

    @Override
    public void onUpdate() {
        for(int x = (int) (mc.player.getX() - 2); x < mc.player.getX() + 2; x++)
            for(int z = (int) (mc.player.getZ() - 2); z < mc.player.getZ() + 2; z++)
                for(int y = (int) (mc.player.getY() - 2); y < mc.player.getY() + 2; y++)
                    if(mc.world.getBlockState(BlockPos.ofFloored(x,y,z)).getBlock() == Blocks.NETHER_PORTAL)
                        confirmTimer.reset();
    }

    @Override
    public String getDisplayInfo() {
        return teleported ? "God" : confirmTimer.getPassedTimeMs() < 5000 ? "Ready" : "Waiting";
    }
  */
}
