package mekanism.common.capabilities;

import java.util.Collection;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.InventoryNetwork;
import mekanism.common.base.ILogisticalTransporter;
import mekanism.common.capabilities.DefaultStorageHelper.NullStorage;
import mekanism.common.content.transporter.TransporterStack;
import mekanism.common.tile.TileEntityLogisticalSorter;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * Created by ben on 03/05/16.
 */
public class DefaultLogisticalTransporter implements ILogisticalTransporter
{
    @Override
    public ItemStack insert(Coord4D original, ItemStack itemStack, EnumColor color, boolean doEmit, int min)
    {
        return null;
    }

    @Override
    public ItemStack insertRR(TileEntityLogisticalSorter outputter, ItemStack itemStack, EnumColor color, boolean doEmit, int min)
    {
        return null;
    }

    @Override
    public void entityEntering(TransporterStack stack, int progress)
    {

    }

    @Override
    public EnumColor getColor()
    {
        return null;
    }

    @Override
    public void setColor(EnumColor c)
    {

    }

    @Override
    public boolean canEmitTo(TileEntity tileEntity, EnumFacing side)
    {
        return false;
    }

    @Override
    public boolean canReceiveFrom(TileEntity tileEntity, EnumFacing side)
    {
        return false;
    }

    @Override
    public double getCost()
    {
        return 0;
    }

    @Override
    public boolean canConnectMutual(EnumFacing side)
    {
        return false;
    }

    @Override
    public boolean canConnect(EnumFacing side)
    {
        return false;
    }

    @Override
    public boolean hasTransmitterNetwork()
    {
        return false;
    }

    @Override
    public InventoryNetwork getTransmitterNetwork()
    {
        return null;
    }

    @Override
    public void setTransmitterNetwork(InventoryNetwork network)
    {

    }

    @Override
    public int getTransmitterNetworkSize()
    {
        return 0;
    }

    @Override
    public int getTransmitterNetworkAcceptorSize()
    {
        return 0;
    }

    @Override
    public String getTransmitterNetworkNeeded()
    {
        return null;
    }

    @Override
    public String getTransmitterNetworkFlow()
    {
        return null;
    }

    @Override
    public String getTransmitterNetworkBuffer()
    {
        return null;
    }

    @Override
    public double getTransmitterNetworkCapacity()
    {
        return 0;
    }

    @Override
    public int getCapacity()
    {
        return 0;
    }

    @Override
    public World world()
    {
        return null;
    }

    @Override
    public Coord4D coord()
    {
        return null;
    }

    @Override
    public Coord4D getAdjacentConnectableTransmitterCoord(EnumFacing side)
    {
        return null;
    }

    @Override
    public IInventory getAcceptor(EnumFacing side)
    {
        return null;
    }

    @Override
    public boolean isValid()
    {
        return false;
    }

    @Override
    public boolean isOrphan()
    {
        return false;
    }

    @Override
    public void setOrphan(boolean orphaned)
    {

    }

    @Override
    public InventoryNetwork createEmptyNetwork()
    {
        return null;
    }

    @Override
    public InventoryNetwork mergeNetworks(Collection<InventoryNetwork> toMerge)
    {
        return null;
    }

    @Override
    public InventoryNetwork getExternalNetwork(Coord4D from)
    {
        return null;
    }

    @Override
    public void takeShare()
    {

    }

    @Override
    public void updateShare()
    {

    }

    @Override
    public Object getBuffer()
    {
        return null;
    }
    
    @Override
    public void setRequestsUpdate() {}

    @Override
    public TransmissionType getTransmissionType()
    {
        return null;
    }

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ILogisticalTransporter.class, new NullStorage<>(), DefaultLogisticalTransporter.class);
    }
}
