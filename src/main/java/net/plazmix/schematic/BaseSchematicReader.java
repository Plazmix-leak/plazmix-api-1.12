package net.plazmix.schematic;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.plazmix.utility.JsonUtil;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

@RequiredArgsConstructor
@Getter
public class BaseSchematicReader {

    private final File schematicFile;

    public Collection<BaseSchematicBlock> read() throws IOException {
        Preconditions.checkArgument(canManage());
        Collection<BaseSchematicBlock> schematicBlocks = new ArrayList<>();

        FileInputStream fileInputStream = new FileInputStream(schematicFile);
        Scanner scanner = new Scanner(fileInputStream);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();

            if (!line.contains(BaseSchematic.SCHEM_BLOCK_DATA_SPLITTER)) {
                continue;
            }

            if (line.replace(" ", "").isEmpty()) {
                continue;
            }

           schematicBlocks.add( parse(line) );
        }

        return schematicBlocks;
    }

    public void write(@NonNull BaseSchematicBlock schematicBlock)
            throws IOException {

        Preconditions.checkArgument(canManage());

        Writer writer = new FileWriter(schematicFile, true)
                .append(schematicBlock.toString())
                .append('\n');

        writer.flush();
        writer.close();
    }

    public BaseSchematicBlock parse(@NonNull String schematicBlockData) {
        String[] splittingData = schematicBlockData.split("\\" + BaseSchematic.SCHEM_BLOCK_DATA_SPLITTER);

        String offsetData   = splittingData[0];
        String stateData    = splittingData[1];

        String[] splittingOffset = offsetData.split(", ");

        Vector vector               = new Vector(Double.parseDouble(splittingOffset[0]), Double.parseDouble(splittingOffset[1]), Double.parseDouble(splittingOffset[2]));
        MaterialData materialData   = JsonUtil.fromJson(stateData, MaterialData.class);


        return new BaseSchematicBlock(vector, materialData);
    }

    private boolean canManage() {
        return schematicFile != null && schematicFile.exists() && schematicFile.getName().endsWith( BaseSchematic.SCHEM_FILE_FORMAT );
    }

}
