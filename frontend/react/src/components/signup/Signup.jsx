import {useAuth} from "../context/AuthContext.jsx";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";
import {Flex, Heading, Image, Link, Stack, Text} from "@chakra-ui/react";
import CreateCustomerForm from "../shared/CreateCustomerForm.jsx";

const Signup = () => {
    const { customer, setCustomerFromToken } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (customer) {
            navigate("/dashboard/customers");
        }
    })

    return (
        <Stack minH={'100vh'} direction={{base: 'column', md: 'row'}}>
            <Flex p={8} flex={1} alignItems={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Image
                        src={"../../images/logo.jpg"}
                        boxSize={"80px"}
                        alt={"Hüseyin AYDIN Logo"}
                        alignSelf={"left"}
                    />
                    <Heading fontSize={'2xl'} mb={12}>Hesap oluştur.</Heading>
                    <CreateCustomerForm onSuccess={(token) => {
                        localStorage.setItem("access_token", token)
                        setCustomerFromToken()
                        navigate("/dashboard");
                    }}/>
                    <Link color={"blue.500"} href={"/"}>
                        Hesabın varsa giriş yap.
                    </Link>
                </Stack>
            </Flex>
            <Flex
                flex={1}
                p={10}
                flexDirection={"column"}
                alignItems={"center"}
                justifyContent={"center"}
                bgGradient={{sm: 'linear(to-r, white.600, white.600)'}}
            >
                <Text fontSize={"5xl"} color={'gray'} fontWeight={"bold"} mb={5}>
                    <Link target={"_blank"} href={"https://github.com/huseyinaydin99"}>
                        Hüseyin AYDIN
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        '/images/background.jpg'
                    }
                />
            </Flex>
        </Stack>
    );
}

export default Signup;