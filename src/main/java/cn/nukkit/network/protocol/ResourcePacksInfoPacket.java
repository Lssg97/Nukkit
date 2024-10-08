package cn.nukkit.network.protocol;

import cn.nukkit.resourcepacks.ResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@ToString
public class ResourcePacksInfoPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET;

    public boolean mustAccept;
    public boolean scripting;
    @Deprecated
    public boolean forceServerPacks; // pre 1.21.30
    public boolean hasAddonPacks;
    public ResourcePack[] behaviourPackEntries = ResourcePack.EMPTY_ARRAY;
    public ResourcePack[] resourcePackEntries = ResourcePack.EMPTY_ARRAY;
    public List<CDNEntry> CDNEntries = new ObjectArrayList<>();

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putBoolean(this.mustAccept);
        this.putBoolean(this.hasAddonPacks);
        this.putBoolean(this.scripting);

        this.encodeResourcePacks(this.resourcePackEntries);

        this.putUnsignedVarInt(this.CDNEntries.size());
        this.CDNEntries.forEach((entry) -> {
            this.putString(entry.getPackId());
            this.putString(entry.getRemoteUrl());
        });
    }

    private void encodeResourcePacks(ResourcePack[] packs) {
        this.putLShort(packs.length);
        for (ResourcePack entry : packs) {
            this.putString(entry.getPackId().toString());
            this.putString(entry.getPackVersion());
            this.putLLong(entry.getPackSize());
            this.putString(entry.getEncryptionKey());
            this.putString(""); // sub-pack name
            this.putString(!entry.getEncryptionKey().isEmpty() ? entry.getPackId().toString() : "");
            this.putBoolean(false); // scripting
            this.putBoolean(false); // isAddonPack
            this.putBoolean(false); // raytracing capable
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Value
    public static class CDNEntry {
        String packId;
        String remoteUrl;
    }
}
