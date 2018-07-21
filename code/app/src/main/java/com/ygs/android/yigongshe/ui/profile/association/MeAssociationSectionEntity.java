package com.ygs.android.yigongshe.ui.profile.association;

import com.chad.library.adapter.base.entity.SectionEntity;

public class MeAssociationSectionEntity extends SectionEntity<String> {

    public MeAssociationSectionEntity(boolean isHeader , String header){
        super(isHeader, header);
    }

    public MeAssociationSectionEntity(String title){
        super(title);
    }
}
