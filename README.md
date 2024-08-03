# xremote-proxy

This is a simple program intended to convert OSC messages between the [Behringer X32 console](https://www.behringer.com/product.html?modelCode=0603-ACE) and [QLC+](http://qlcplus.org/). My use case is to control my light show (QLC+) via user assignable buttons on the X32 console. Though this program probably can be extended for your own needs. (Feel free to hack away at it!)

`xremote-proxy` works by converting the "Button press" OSC messages that the X32 generates to OSC messages that QLC+ understands. QLC+ only works with float values, while the X32 works with integer values. Also, the X32 will not send OSC messages by itself. It needs to be persuaded to do so by sending an `/xremote` OSC message. (That message is actually not valid OSC, but hey...)

Other programs that send OSC messages in either X32 format or QLC+ format are also supported. For example [TouchOSC](https://hexler.net/touchosc).

## What to expect / what not to expect

This program only supports the user assignable buttons and rotary encoders found on the X32 consoles. Probably other OSC messages could also be captured and converted, but I have no need for that.

When triggering the user assignable button on the X32, the function assigned to it will trigger,  _and_  an OSC message will be sent.
This OSC message will be converted by `xremote-proxy` and passed on to QLC+.
There, the button assigned to the OSC message will trigger.

The other way around will _not_ trigger the function assigned to the X32 user assignable button. So, when triggering the button from QLC+, the user assignable button on the X32 will light up, but the function assigned to it will  _not_  trigger.

## How I use it

Since I only want to control QLC+ using the user assignable buttons, I need to assign a 'dummy' function to my user assignable buttons.
I do this by assigning "send MIDI note" functions. I don't use MIDI, so this has no effect on my setup.

I also use TouchOSC which emulates X32 "Button press" OSC messages.

The end result is:
- pressing a button on the X32 will trigger a button in QLC+ and will light up the button in TouchOSC;
- pressing a button in QLC+ will light up the user assignable button on the X32 and will light up the button in TouchOSC;
- pressing a button in TouchOSC will trigger a button in QCL+ and will light up the user assignable button on the X32

## Usage

Start the application by running it from a command line. (It requires Java 21+.)

`java -jar ./target/xremote-proxy-0.0.1.jar` (update the version number when applicable)

You'll see an error message explaining that you should give some mandatory parameters. Give them, and run again.

I'll explain the most important command line arguments here.

### --x32

This is the address of your X32 console. You can give either an IP address or a hostname. You can also give a port number, but this will most likely be the default `10023`.

Examples:
- `--x32 192.168.0.1`
- `--x32 console.local.lan`
- `--x32 172.0.0.1:1111` (for example, if you run [Patrick-Gilles Maillot's X32 emulator](https://github.com/pmaillot/X32-Behringer) on `localhost` port `1111`).

### --x32-listen

This is the host+port where `xremote-proxy` will listen on for incoming OSC messages sent by the X32 console (or an OSC client emulating the X32 console).

### --qlcplus

This is the address of QLC+. You can give either an IP address or a hostname. You can also give a port number. By default the port is `7700`.

Examples:
- `--qlcplus 192.168.0.2`
- `--qlcplus lighting.local.lan`
- `--qlcplus 172.0.0.1:7711` (for example if you run QLC+ on the same machine as `xremote-proxy` and on port `7711`)

### --qlcplus-listen

This is the host+port where `xremote-proxy` will listen on for incoming OSC messages sent by QLC+ (or an OSC client emulating QLC+).

## "Fake" OSC clients

If you have a third party OSC client that sends/receives messages just like the ones that QLC+ sends/receives, you should:
- direct that client to send to the host+port you gave with `--qlcplus-listen`;
- add another `--qlcplus` command line option with the host+port where `xremote-proxy` should send OSC messages to your client to

If you have a third party OSC client that sends/receives messages just like the ones that the X32 sends/receives, you should:
- direct that client to send to the host+p;ort you gave with `--x32-listen`;
- add an `--fake-x32` command line option with the host+port where `xremote-proxy` should send OSC messages to your client to

### --fake-x32 vs --x32

Indeed, the option `--fake-x32` is nearly identical to `--x32`. The only difference is that `xremote-proxy` will send `/xremote` OSC messages to hosts given with `--x32`, while the hosts given with `--fake-x32` will not receive `/xremote` OSC messages from `xremote-proxy`.

## Stuff in the `res` folder

### QLC+ input profile.qxi

QLC+ works with [input profiles](https://docs.qlcplus.org/v4/input-output/input-profiles). This is where you define how QLC+ should interpret the OSC messages received. For a correct working of `xremote-proxy` you should use this profile. Install it by copying the QXI file to `~/.qlcplus/inputprofiles/` and restart QLC+. Then navigate to the `Inputs/Outputs` tab → `Profile` tab and select the `Jurrie Overgoor Behringer X32 via xremote-proxy` profile.

### QLC+ template project.qxw

This is QLC+ project that "emulates" the X32 user assignable buttons. When you want to set up your own `X32 ↔ xremote-proxy ↔ QLC+`, you could use this as a starting point for QLC+. Note that the project does not contain any useful functionality. You should see the X32 user assignable buttons light up when you press them in QLC+, and you should see the QLC+ buttons light up when you press them on the X32. Nothing more.

### TouchOSC emulating X32.tosc

This is a project for [TouchOSC](https://hexler.net/touchosc) that will emulate the X32 sending OSC messages. You can use this project in combination with the `--fake-x32` command line argument of `xremote-proxy`.

### TouchOSC emulating QLC+.tosc

This is a project for [TouchOSC](https://hexler.net/touchosc) that will emulate QLC+ sending OSC messages using the `QLC+ input profile` profile. You can use this project in combination with the `--qlcplus` command line argument of `xremote-proxy`.

# Legal stuff

Behringer and the Behringer X32 are registered trademarks by Music Tribe. I am not affiliated in any way.

TouchOSC is created by Hexler Limited. I am not affiliated in any way.

QLC+ is created by the QLC+ Development Team. I am not affiliated in any way.
