# xremote-proxy

This is a simple program intended to convert OSC messages between the [Behringer X32 console](https://www.behringer.com/product.html?modelCode=0603-ACE) and [QLC+](http://qlcplus.org/). My use case is to control my light show (QLC+) via user assignable buttons on the X32 console. Though this program probably can be extended for your own needs. (Feel free to hack away at it!)

`xremote-proxy` works by converting the "Button press" OSC messages that the X32 generates to OSC messages that QLC+ understands. QLC+ only works with float values, while the X32 works with integer values. Also, the X32 will not send OSC messages by itself. It needs to be persuaded to do so by sending an `/xremote` OSC message. (That message is actually not valid OSC, but hey...)

Other programs that send OSC messages in either X32 format or QLC+ format are also supported. For example [TouchOSC](https://hexler.net/touchosc).

## What to expect / what not to expect

This program only supports the mutegroup buttons and the user assignable buttons and rotary encoders found on the X32 consoles. Probably other OSC messages could also be captured and converted, but I have no need for that.

### Note when using user assignable buttons and encoders
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
- pressing a button in TouchOSC will trigger a button in QLC+ and will light up the user assignable button on the X32

## Usage

Start the application by running it from a command line. (It requires Java 21+.)

`java -jar ./target/xremote-proxy-<version>.jar` (update the version number when applicable)

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

### --wing

This is the host where `xremote-proxy` will try to find a Behringer WING Compact console. If found, user assignable buttons 1 to 8 are mapped to X32 buttons 1 to 8 (bank C).

Support for WING Compact is preliminary! The mapping is highly specific to my use case. It would be better to rewrite this to some config-file based mapping, so users can configure the mapping between X32/WING/QLC+.

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

# Debian packages

Debian packages for AMD64 and ARM64 (which is the architecture of Raspberry Pi for example) are provided. You install these packages like normal: `apt install xremote-proxy-<version>.deb`.
A Systemd service is created. But be advised that the default command to start `xremote-proxy` will not suffice. You'll need to specify your own command. To do this, do a `systemctl edit xremote-proxy.service` and give the following lines:

```
[Service]
ExecStart=
ExecStart=/opt/xremote-proxy/bin/xremote-proxy --x32 <your-x32-ip-here> --qlcplus <your-qlc+-ip-here> <other-arguments-here>
```

This will create the file `/etc/systemd/system/xremote-proxy.service.d/override.conf` which will override the defaults of the Systemd service. Then do a `systemctl daemon-reload` and start the service with `systemctl start xremote-proxy`. If things go wrong please check the output of `systemctl status xremote-proxy.service` and `journalctl -u xremote-proxy.service`. If you want the service to start at boot, do a `systemctl enable xremote-proxy`.

# Building

A single `mvn package` builds the jar *and* the Debian packages for both AMD64 and ARM64. You don't need an ARM machine: the ARM64 package is built by a real ARM64 JDK running under QEMU emulation.

## Prerequisites

- **JDK 25** with `jdeps` on your `PATH`. If you use [SDKMAN!](https://sdkman.io/), run `sdk env install` in the project directory to get the version from `.sdkmanrc`.
- **Maven 3.9.0** or newer.
- **Podman** and **QEMU user-mode emulation**. On Debian or Ubuntu:

  ```
  sudo apt install podman qemu-user-static
  ```

  The `qemu-user-static` package registers QEMU with the kernel's `binfmt_misc`, so ARM64 binaries run transparently, also inside containers. Check that it works with:

  ```
  podman run --rm --platform linux/arm64 docker.io/library/eclipse-temurin:25-jdk-noble uname -m
  ```

  This should print `aarch64`.

## Build

```
mvn package
```

This will:

1. compile the code and build a jar with all dependencies included (`target/xremote-proxy-<version>.jar`);
2. run `jdeps` on that jar to find out which JDK modules it needs, and write them to `modules.txt`;
3. copy the `Dockerfile` to `target/`, filling in values from the `pom.xml` (version, name, Java version, etc.);
4. run `podman build --platform linux/amd64,linux/arm64` with that `Dockerfile`. This runs the `Dockerfile` once for each architecture, the ARM64 run under QEMU emulation. The builder stage uses `jlink` to create a minimal Java runtime with the modules from `modules.txt`, and `jpackage` to wrap that runtime and the jar into a `.deb`. A final `scratch` stage exports the `.deb`, so both packages end up in `target/`.

The result is in `target/`:

- `xremote-proxy-<version>.jar`
- `xremote-proxy_<version>_amd64.deb`
- `xremote-proxy_<version>_arm64.deb`

`modules.txt` is under version control on purpose. If it changes after a build, a (new) dependency needs an extra JDK module. Check the change and commit it.

Expect the ARM64 build to be quite a bit slower than the AMD64 one. That's the price of emulation.

## Troubleshooting

- **`exec format error` during the ARM64 build**: QEMU is not registered with `binfmt_misc`. Make sure `qemu-user-static` is installed, and check that `/proc/sys/fs/binfmt_misc/qemu-aarch64` exists. A reboot or `sudo systemctl restart systemd-binfmt` may be needed after installing.
- **`jdeps: command not found`**: the JDK's `bin` directory is not on your `PATH`.

## Testing the Debian package

`Dockerfile.test` builds a Debian container with systemd that installs the freshly built `.deb`:

```
podman build -f Dockerfile.test -t xremote-proxy .
podman run --rm -it --name xremote-proxy xremote-proxy
```

You can log in as `root` with password `xremote-proxy`, and check the service with `systemctl status xremote-proxy`.

# Legal stuff

Behringer and the Behringer X32 are registered trademarks by Music Tribe. I am not affiliated in any way.

TouchOSC is created by Hexler Limited. I am not affiliated in any way.

QLC+ is created by the QLC+ Development Team. I am not affiliated in any way.
