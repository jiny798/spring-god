<template>
    <list-item></list-item>
</template>

<script>
import ListItem from '../components/ListItem.vue';
import bus from '../utils/bus.js';

import ListMixin from '../mixins/ListMixin.js';
export default {
    components: {
        ListItem,
    },
    mixins: [ListMixin],
    created() {
        // bus.$emit('start:spinner');
        // this.$store
        //     .dispatch('FETCH_NEWS')
        //     .then(() => {
        //         console.log('fetched');
        //         bus.$emit('end:spinner');
        //     })
        //     .catch((error) => {
        //         console.log(error);
        //     });
        // bus.$emit('end:spinner');
    },
    // mounted() {
    //     bus.$emit('end:spinner');
    // },
};
</script>

<style></style>
