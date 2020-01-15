//
//  ViewController.h
//  JitsiMobile
//
//  Created by Sébastien Krafft on 07/08/2018.
//  Copyright © 2018 Sébastien Krafft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <JitsiMeet/JitsiMeet.h>

@interface JitsiMeetViewController : UIViewController

- (void) setDelegate:(id<JitsiMeetViewDelegate>) delegate;
- (void)loadUrl:(NSString *)room isVideo:(BOOL)isVideo avatarUrl:(NSString *)avatarUrl displayName:(NSString *)displayName;
- (void)endCall;

@end

