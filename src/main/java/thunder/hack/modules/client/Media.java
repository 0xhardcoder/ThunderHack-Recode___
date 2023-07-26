package thunder.hack.modules.client;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import thunder.hack.events.impl.PacketEvent;
import thunder.hack.injection.accesors.IGameMessageS2CPacket;
import thunder.hack.modules.Module;
import thunder.hack.setting.Setting;
import thunder.hack.utility.Util;

public class Media extends Module {
    public Media() {
        super("Media", Category.CLIENT);
    }

    public Setting<Boolean> skinProtect = new Setting<>("SkinProtect", true);
    public Setting<Boolean> nickProtect = new Setting<>("NickProtect", true);

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive e){
        if(e.getPacket() instanceof GameMessageS2CPacket pac && nickProtect.getValue()) {
            for (PlayerListEntry ple : Util.mc.player.networkHandler.getPlayerList()){
                if(pac.content().getString().contains(ple.getDisplayName().getString())){
                    IGameMessageS2CPacket pac2 = (IGameMessageS2CPacket) e.getPacket();
                    pac2.setContent(Text.of(pac.content().getString().replace(ple.getDisplayName().getString(),"Protected")));
                }
            }
        }
    }

}