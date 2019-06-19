//
//  ImageView.h
//  RCTImagePreview
//
//  Created by 欧阳伟坚 on 2018/3/21.
//  Copyright © 2018年 欧阳伟坚. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ImageView : UIView

-(void)getImageData:(NSArray *)data index:(CGFloat)num isShowSave:(BOOL)save;

@end
